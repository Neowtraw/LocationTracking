package com.codingub.locationtracking.ui.auth

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)