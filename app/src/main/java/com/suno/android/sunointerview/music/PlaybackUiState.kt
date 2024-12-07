package com.suno.android.sunointerview.music

import androidx.media3.common.MediaMetadata

data class PlaybackUiState (
    val isPlaying: Boolean = true,
    val metadataList: List<MediaMetadata> = emptyList(),
)
