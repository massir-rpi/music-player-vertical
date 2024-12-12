package com.suno.android.sunointerview.inject

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.suno.android.sunointerview.service.PlaybackService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ViewModelComponent::class)
@Module
object PlayerModule {

    @Provides
    fun provideMediaControllerFuture(@ApplicationContext appContext: Context) = MediaController.Builder(
        appContext,
        SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java)),
    ).buildAsync()
}
