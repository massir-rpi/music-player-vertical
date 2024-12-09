package com.suno.android.sunointerview.music

import android.net.Uri

data class PlaybackUiState (
    val isPlaying: Boolean = true,
    val currentTime: Float = 0F,
    val currentMediaIndex: Int = 0,
    val metadataList: List<SongMetadata> = emptyList(),
)

data class SongMetadata (
    val avatarImageUri: Uri? = null,
    val authorName: String? = null,
    val durationMs: Float? = null,
    val imageUri: Uri? = null,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val title: String? = null,
    val upvoteCount: Int? = null,
    val shareUrl: String? = null,
)
