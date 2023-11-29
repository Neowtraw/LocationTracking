package com.codingub.locationtracking

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.codingub.locationtracking.databinding.ActivityMainBinding
import com.codingub.locationtracking.ui.auth.GoogleAuthUiClient
import dagger.hilt.android.AndroidEntryPoint
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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        if (googleAuthUiClient.getSignedInUser() == null) {
            navController.navigate(R.id.loginFragment)
            return
        }
        navController.navigate(R.id.trackingFragment)

    }
}