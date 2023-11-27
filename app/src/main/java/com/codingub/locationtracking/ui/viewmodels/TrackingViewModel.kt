package com.codingub.locationtracking.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class TrackingViewModel @Inject constructor() : ViewModel() {

    // The location request that defines the location updates
    var locationRequest: MutableStateFlow<LocationRequest?> = MutableStateFlow(null)
    // Keeps track of received location updates as text
    var locationUpdates: MutableStateFlow<String> = MutableStateFlow("")
}