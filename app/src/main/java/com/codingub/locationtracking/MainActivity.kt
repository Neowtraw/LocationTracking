package com.codingub.locationtracking

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.codingub.locationtracking.databinding.ActivityMainBinding
import com.codingub.locationtracking.ui.auth.GoogleAuthUiClient
import com.codingub.locationtracking.ui.fragments.LoginFragment
import com.codingub.locationtracking.ui.fragments.TrackingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val mainViewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (googleAuthUiClient.getSignedInUser() != null) {

            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, TrackingFragment())
                    .commit()
            }
        } else {
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, LoginFragment())
                .commit()
        }

        observeChanges()
    }


    private fun observeChanges() {
        with(mainViewModel) {
            lifecycleScope.launch(Dispatchers.Main) {

                if (state.value.isSignInSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Sign in successful",
                        Toast.LENGTH_LONG
                    ).show()
                    supportFragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, TrackingFragment())
                        .commit()
                }
                if (googleAuthUiClient.getSignedInUser() != null) {
                    Toast.makeText(
                        applicationContext,
                        "Sign in successful",
                        Toast.LENGTH_LONG
                    ).show()
                    supportFragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, TrackingFragment())
                        .commit()
                }
            }
        }
    }
}