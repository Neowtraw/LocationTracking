package com.codingub.locationtracking.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.codingub.locationtracking.R
import com.codingub.locationtracking.databinding.FragmentTrackingBinding
import com.codingub.locationtracking.ui.geo.GeofenceManager
import com.codingub.locationtracking.ui.viewmodels.TrackingViewModel
import com.codingub.locationtracking.utils.BaseFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("MissingPermission")
@AndroidEntryPoint
class TrackingFragment : BaseFragment() {

    // base
    private val trackingViewModel by viewModels<TrackingViewModel>()
    private lateinit var binding: FragmentTrackingBinding

    // geo location
    @Inject
    lateinit var geofenceManager: GeofenceManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationInfo: String? = null
    private lateinit var marker: PlacemarkMapObject

    private lateinit var locationCallback: LocationCallback

    // geo permissions
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private lateinit var backgroundPermissionRequest: ActivityResultLauncher<String>

    companion object {
        const val ZOOM_VALUE = 16.5f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // main permissions
        locationPermissionRequest =
            requireActivity().registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        createLocationRequest(true)

                    }

                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        createLocationRequest(false)
                    }

                    else -> {

                    }
                }
            }

        // background location permission
        backgroundPermissionRequest =
            requireActivity().registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )

        // background location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundPermissionRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        createLocationEffect()
        createLocationRequest(true)
    }

    override fun onCreateView(
        inf: LayoutInflater,
        con: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackingBinding.inflate(inf, con, false)
        MapKitFactory.initialize(requireContext())
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
        startLocationUpdates()
        createMarkerInCurrentLocation()
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    /*
        Geo updates
     */

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    private fun createLocationRequest(usePreciseLocation: Boolean) {
        val priority = if (usePreciseLocation) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }
        trackingViewModel.locationRequest.value =
            LocationRequest.Builder(priority, TimeUnit.SECONDS.toMillis(3)).build()
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            trackingViewModel.locationRequest.value!!,
            locationCallback,
            null
        )
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    private fun createLocationEffect() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                moveToCurrentLocation(Point(location.latitude, location.longitude))
                updateMarkerInCurrentLocation(Point(location.latitude, location.longitude))
            }
        }
    }

    /*
        Additional
     */

    private fun moveToCurrentLocation(currentLocation: Point) {
        binding.mapview.mapWindow.map.move(
            CameraPosition(currentLocation, ZOOM_VALUE, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )
    }

    private fun createMarkerInCurrentLocation() {
        val mapObject = binding.mapview.mapWindow.map.mapObjects

        marker = mapObject.addPlacemark()
        marker.setIcon(
            ImageProvider.fromResource(
                requireContext(),
                R.drawable.marker
            )
        )
    }

    private fun updateMarkerInCurrentLocation(currentLocation: Point) {
        marker.geometry = currentLocation

    }


    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    // getting last known location is faster and minimizes battery usage
    private fun getLastKnownLocation() {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(NonCancellable) {
                val result = fusedLocationClient.lastLocation.await()
                locationInfo = if (result == null) {
                    "No last known location. Try fetching the current location first"
                } else {
                    "Current location is \n" + "lat : ${result.latitude}\n" +
                            "long : ${result.longitude}\n" + "fetched at ${System.currentTimeMillis()}"
                }
            }
        }
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    //To get more accurate or fresher device location use this method
    private fun getCurrentLocation(usePreciseLocation: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            val priority = if (usePreciseLocation) Priority.PRIORITY_HIGH_ACCURACY
            else Priority.PRIORITY_BALANCED_POWER_ACCURACY

            val result = fusedLocationClient.getCurrentLocation(
                priority,
                CancellationTokenSource().token,
            ).await()
            result.let { fetchedLocation ->
                locationInfo =
                    "Current location is \n" + "lat : ${fetchedLocation.latitude}\n" +
                            "long : ${fetchedLocation.longitude}\n" + "fetched at ${System.currentTimeMillis()}"
            }
        }
    }
}