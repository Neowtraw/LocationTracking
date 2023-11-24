package com.codingub.locationtracking.ui.fragments

import com.codingub.locationtracking.ui.viewmodels.TrackingViewModel
import com.codingub.locationtracking.utils.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : BaseFragment() {

    private lateinit var trackingViewModel: TrackingViewModel
}