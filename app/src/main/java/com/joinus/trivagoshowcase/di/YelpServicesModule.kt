package com.joinus.trivagoshowcase.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import services.network.RetrofitInstance
import services.network.YelpServices
import javax.inject.Singleton

@InstallIn(ActivityComponent::class)
@Module
class YelpServicesModule {

    @Singleton
    @Provides
    fun provideImplementation(): YelpServices {
        return RetrofitInstance.getYelpServices()
    }
}