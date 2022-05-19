package com.joinus.trivagoshowcase.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import services.network.RetrofitInstance
import services.network.YelpServices
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class YelpServicesModule {

    @Singleton
    @Provides
    fun provideImplementation(): YelpServices {
        return RetrofitInstance.getYelpServices()
    }
}