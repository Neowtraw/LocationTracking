package com.codingub.locationtracking.di

import android.content.Context
import com.codingub.locationtracking.ui.geo.GeofenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeoModule {

    @Provides
    @Singleton
    fun provideGeofenceManager(@ApplicationContext context: Context) =
        GeofenceManager(context)
}