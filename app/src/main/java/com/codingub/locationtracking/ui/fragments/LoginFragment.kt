package com.codingub.locationtracking.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.codingub.locationtracking.R
import com.codingub.locationtracking.databinding.FragmentLoginBinding
import com.codingub.locationtracking.ui.auth.GoogleAuthUiClient
import com.codingub.locationtracking.utils.BaseFragment
import com.codingub.locationtracking.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment() : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding

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
        return binding.root
    }


    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener { view ->
            lifecycleScope.launch {
                view.findNavController().navigate(R.id.action_loginFragment_to_trackingFragment)

                val intent = googleAuthUiClient.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        intentSender = intent ?: return@launch
                    ).build()
                )
            }
        }
    }
}