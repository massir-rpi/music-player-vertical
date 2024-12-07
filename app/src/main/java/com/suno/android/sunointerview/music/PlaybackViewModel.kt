package com.suno.android.sunointerview.music

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import com.suno.android.sunointerview.api.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
) : ViewModel() {
    private val _uiStateFlow = MutableStateFlow(PlaybackUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private var page = 0
    private lateinit var player: ExoPlayer

    fun setPlayer(player: ExoPlayer) {
        this.player = player
        nextSong()
    }

    fun togglePlaying() {
        val uiState = _uiStateFlow.value
        val notIsPlaying = !uiState.isPlaying
        player.playWhenReady = notIsPlaying
        _uiStateFlow.value = uiState.copy(
            isPlaying = notIsPlaying,
            metadata = uiState.metadata,
        )
    }

    fun nextSong() {
        if (!player.hasNextMediaItem()) {
            loadNextPage()
        } else {
            player.seekToNextMediaItem()
        }
    }

    private fun loadNextPage() {
        page += 1
        viewModelScope.launch {
            musicRepository.getSongs(page, SONGS_PER_PAGE).body()?.songs?.let {
                loadMediaIntoPlayer(player, it)
            }
        }
    }

    private fun loadMediaIntoPlayer(exoPlayer: ExoPlayer, songs: List<Song?>) {
        exoPlayer.addMediaItems(songsToMediaList(songs))
        exoPlayer.prepare()
        if (player.hasPreviousMediaItem()) player.seekToNextMediaItem()
        player.playWhenReady = true

        val uiState = _uiStateFlow.value
        _uiStateFlow.value = uiState.copy(
            isPlaying = true, // always auto-play next song
            metadata = player.mediaMetadata
        )
    }

    private fun songsToMediaList(songs: List<Song?>) = songs.mapNotNull { songNullable ->
        songNullable?.let { song ->
            MediaItem.Builder().apply {
                setUri(song.audioUrl)
                song.id?.let { setMediaId(it) }
                setMediaMetadata(
                    MediaMetadata.Builder().apply {
                        song.title?.let { setTitle(it) }
                        song.imageUrl?.let { setArtworkUri(Uri.parse(it)) }
                        song.displayName?.let { setArtist(it) }
                        song.metadata?.tags?.let { setDescription(it) }
                    }.build()
                )
            }.build()
        }
    }

    private companion object {
        const val SONGS_PER_PAGE = 10
    }
}
