package com.codingub.locationtracking.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
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
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
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

    // geo permissions
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // main permissions
        locationPermissionRequest =
            requireActivity().registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {}

                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    }
                    permissions.getOrDefault(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        false
                    ) -> {}
                    else -> {
                    }
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
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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
    }
    override fun onStop() {
        super.onStop()
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }


    fun createLocationRequest(usePreciseLocation: Boolean) {
        val priority = if (usePreciseLocation) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }
        LocationRequest.Builder(priority, TimeUnit.SECONDS.toMillis(3)).build()
    }



    /*
        Additional
     */
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