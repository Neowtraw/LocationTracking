package com.codingub.locationtracking.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.codingub.locationtracking.R
import com.codingub.locationtracking.databinding.FragmentLoginBinding
import com.codingub.locationtracking.ui.auth.GoogleAuthUiClient
import com.codingub.locationtracking.ui.viewmodels.LoginViewModel
import com.codingub.locationtracking.utils.BaseFragment
import com.codingub.locationtracking.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment() : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel : LoginViewModel by viewModels()

    @Inject
    lateinit var log: Logger
    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient
    @Inject
    lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>


    override fun onCreateView(
        inf: LayoutInflater,
        con: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inf, con, false)

        setupListeners()
        observeChanges()
        return binding.root
    }


    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener { view ->
            lifecycleScope.launch {
                val intent = googleAuthUiClient.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        intentSender = intent ?: return@launch
                    ).build()
                )
                withContext(Dispatchers.Main) {
                    if (googleAuthUiClient.getSignedInUser() != null) {
                        loginViewModel.setCanMove(true)
                    }
                }
            }
        }
    }

    override fun observeChanges() {
        with(loginViewModel){
            lifecycleScope.launch {
                canMove.collectLatest {
                    if(it) findNavController().navigate(R.id.action_loginFragment_to_trackingFragment)
                }
            }
        }
    }
}