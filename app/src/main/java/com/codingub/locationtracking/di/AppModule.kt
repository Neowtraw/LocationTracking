package com.codingub.locationtracking.di

import com.codingub.locationtracking.utils.HistoryLogger
import com.codingub.locationtracking.utils.Logger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun bindsLogger(bind: HistoryLogger): Logger
}