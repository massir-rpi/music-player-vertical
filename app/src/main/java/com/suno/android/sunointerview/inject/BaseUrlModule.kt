package com.suno.android.sunointerview.inject

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object BaseUrlModule {
    private const val REST_API_URL = "https://apitest.suno.com/api/"

    @RestUrl
    @Provides
    @Singleton
    fun provideRestUrl(): String = REST_API_URL
}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class RestUrl
