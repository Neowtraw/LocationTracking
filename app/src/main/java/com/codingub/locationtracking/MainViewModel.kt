package com.codingub.locationtracking

import android.util.Log
import androidx.lifecycle.ViewModel
import com.codingub.locationtracking.ui.auth.SignInResult
import com.codingub.locationtracking.ui.auth.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )

        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}