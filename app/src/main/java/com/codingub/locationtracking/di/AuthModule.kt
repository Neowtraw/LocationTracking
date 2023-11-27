package com.codingub.locationtracking.di

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.codingub.locationtracking.MainActivity
import com.codingub.locationtracking.ui.auth.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.codingub.locationtracking.utils.Logger


@Module
@InstallIn(ActivityComponent::class)
object AuthModule {

    @Provides
    @ActivityScoped
    fun provideLauncher(
        @ActivityContext context: Context,
        googleAuthUiClient: GoogleAuthUiClient
    ): ActivityResultLauncher<IntentSenderRequest> {
        val activity = context as MainActivity

        return activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                activity.lifecycleScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    activity.mainViewModel.onSignInResult(signInResult)
                }
            }
        }
    }

    @Provides
    fun provideGoogleAuthUiClient(@ApplicationContext context: Context): GoogleAuthUiClient {
        val googleAuthUiClient by lazy {
            GoogleAuthUiClient(
                context = context,
                oneTapClient = Identity.getSignInClient(context)
            )
        }
        return googleAuthUiClient
    }
}