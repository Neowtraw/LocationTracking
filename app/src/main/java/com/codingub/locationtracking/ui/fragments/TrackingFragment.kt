package com.codingub.locationtracking.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.codingub.locationtracking.R
import com.codingub.locationtracking.databinding.FragmentTrackingBinding
import com.codingub.locationtracking.ui.custom.AlertDialog
import com.codingub.locationtracking.ui.custom.AlertDialogPermissionBuilder
import com.codingub.locationtracking.ui.custom.AlertDialogPermissionView
import com.codingub.locationtracking.ui.geo.GeofenceManager
import com.codingub.locationtracking.ui.utils.dp
import com.codingub.locationtracking.ui.utils.openAppSettings
import com.codingub.locationtracking.ui.viewmodels.TrackingViewModel
import com.codingub.locationtracking.utils.BaseFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import kotlin.math.roundToInt


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
    private var hasCoarseLocPermission = false
    private var alertDialog: AlertDialog? = null

    companion object {
        const val ZOOM_VALUE = 16.5f
    }


    private var prevOffset = 0f



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        createLocationEffect()

        locationPermissionRequest =
            requireActivity().registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val hasFineLocPermission =
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
                hasCoarseLocPermission =
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

                if (hasFineLocPermission && hasCoarseLocPermission) {
                    setLocationSettings(true)
                } else if (hasCoarseLocPermission) {
                    setLocationSettings(false)
                } else {
                    createAlertPermissionDialog()
                }
            }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    override fun onCreateView(
        inf: LayoutInflater,
        con: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackingBinding.inflate(inf, con, false)
        MapKitFactory.initialize(requireContext())

        createBottomSheetInformation()
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()

        if(hasCoarseLocPermission) startLocationUpdates()
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

    private fun setLocationSettings(usePreciseLocation: Boolean) {
        createLocationRequest(usePreciseLocation)
        createMarkerInCurrentLocation()
        startLocationUpdates()
    }


    private fun createLocationRequest(usePreciseLocation: Boolean) {
        val priority = if (usePreciseLocation) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }
        trackingViewModel.locationRequest.value =
            LocationRequest.Builder(priority, TimeUnit.SECONDS.toMillis(3)).build()
    }


    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            trackingViewModel.locationRequest.value!!,
            locationCallback,
            null
        )
    }


    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

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

    private fun createBottomSheetInformation() {

    }

    // camera
    private fun moveToCurrentLocation(currentLocation: Point) {
        binding.mapview.mapWindow.map.move(
            CameraPosition(currentLocation, ZOOM_VALUE, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )
    }

    // marker
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

    private fun deleteMarker() = binding.mapview.mapWindow.map.mapObjects.remove(marker)

    // alert permission dialog
    private fun createAlertPermissionDialog() {
        if (alertDialog != null) return

        val view = AlertDialogPermissionView(requireContext(),
            AlertDialogPermissionBuilder(
                warning = R.string.location_permission_warning,
                positiveText = R.string.accept_permissions,
                positiveOnClick = {
                    requireActivity().openAppSettings()
                    alertDialog?.dismiss()
                }
            ))
        alertDialog = AlertDialog(requireContext()).apply {
            setView(view)
            setOnDismissListener {
                alertDialog = null
            }
        }.also { it.show() }
    }

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