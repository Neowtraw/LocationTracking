package com.codingub.locationtracking.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _canMove: MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(false)
    val canMove = _canMove.asStateFlow()

    fun setCanMove(value: Boolean) {
        _canMove.value = value
    }

}