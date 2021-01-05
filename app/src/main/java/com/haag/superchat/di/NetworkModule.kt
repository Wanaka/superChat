package com.haag.superchat.di

import com.haag.superchat.retrofit.NotificationApi
import com.haag.superchat.util.FCMConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideNotificationService(): NotificationApi {
        return Retrofit.Builder()
            .baseUrl(FCMConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationApi::class.java)
    }
}












