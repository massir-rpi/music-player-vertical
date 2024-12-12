package com.suno.android.sunointerview.service

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    // Build ExoPlayer and MediaSession in MediaSessionService onCreate
    override fun onCreate() {
        super.onCreate()
        buildMediaSession()
    }

    // The user dismissed the app from the recent tasks
    @OptIn(UnstableApi::class)
    override fun onTaskRemoved(rootIntent: Intent?) {
        // Destroy session and stop service when task is removed from overview menu
        destroyMediaSession()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    // Always grant access to session
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    // Release the session and player
    override fun onDestroy() {
        destroyMediaSession()
        super.onDestroy()
    }

    private fun buildMediaSession() {
        val player = ExoPlayer.Builder(this).build()
        // Video will not be rendered since this is audio playback
        player.setVideoSurface(null)
        mediaSession = MediaSession.Builder(this, player).build()
    }

    private fun destroyMediaSession() {
        mediaSession?.player?.release()
        mediaSession?.release()
        mediaSession = null
    }
}
