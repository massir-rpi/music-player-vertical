package com.suno.android.sunointerview.music

import androidx.media3.common.MediaMetadata

data class PlaybackUiState (
    val isPlaying: Boolean = true,
    val currentTime: Float = 0F,
    val currentMediaIndex: Int = 0,
    val metadataList: List<MediaMetadata> = emptyList(),
)
