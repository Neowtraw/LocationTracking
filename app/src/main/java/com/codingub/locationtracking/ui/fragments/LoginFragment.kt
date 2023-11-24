package com.codingub.locationtracking.ui.fragments

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.codingub.locationtracking.MainActivity
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

    @Inject lateinit var log: Logger
    @Inject lateinit var googleAuthUiClient: GoogleAuthUiClient
    @Inject lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>


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
        binding.btnSignIn.setOnClickListener {
            lifecycleScope.launch {
                val intent = googleAuthUiClient.signIn()

                log.d(intent.toString())
                launcher.launch(
                    IntentSenderRequest.Builder(
                        intentSender = intent ?: return@launch
                    ).build()
                )
            }
        }
    }
}