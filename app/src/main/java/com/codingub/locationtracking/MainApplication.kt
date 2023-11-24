package com.codingub.locationtracking

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class MainApplication : Application() {

    companion object {
        private var Instance: MainApplication? = null
        fun getInstance(): MainApplication = Instance!!
    }

    init {
        Instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}