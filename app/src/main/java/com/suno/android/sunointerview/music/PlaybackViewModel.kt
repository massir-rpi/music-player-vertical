package com.suno.android.sunointerview.music

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
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

    private lateinit var player: ExoPlayer

    private var numPages = 0
    var numSongs = 0
        private set

    fun setPlayer(player: ExoPlayer) {
        Log.d(SCREEN_NAME, "Setting player in view model")
        this.player = player
        // Load first page and start playing the first song after setting the player
        loadNextPage(0)
    }

    fun togglePlaying() {
        val uiState = _uiStateFlow.value
        val notIsPlaying = !uiState.isPlaying
        player.playWhenReady = notIsPlaying
        _uiStateFlow.value = uiState.copy(
            isPlaying = notIsPlaying,
            metadataList = uiState.metadataList,
        )
    }

    fun seekToMediaItem(index: Int) {
        // Load the next page of songs if we're near the end of the current page
        Log.d(SCREEN_NAME, "View model seekToMediaItem called wiht index $index on playlist of size ${player.mediaItemCount}")
        if (index >= player.mediaItemCount - 2) {
            loadNextPage(index)
        } else {
            // Seek to the song at the given index
            Log.d(SCREEN_NAME, "Seeking to $index in playlist of size ${player.mediaItemCount}")
            player.seekTo(index, C.TIME_UNSET)
            // Always auto-play next song
            if (!uiStateFlow.value.isPlaying) {
                togglePlaying()
            }
        }
    }

    private fun loadNextPage(indexSeekTo: Int) {
        viewModelScope.launch {
            Log.d(SCREEN_NAME, "Loading next page")
            musicRepository.getSongs(numPages, SONGS_PER_PAGE).body()?.songs?.let {
                numPages += 1
                numSongs += SONGS_PER_PAGE
                loadMediaIntoPlayer(it)
                seekToMediaItem(indexSeekTo)
            }
        }
    }

    private fun loadMediaIntoPlayer(songs: List<Song?>) {
        Log.d(SCREEN_NAME, "Adding page to media list and preparing for playback")
        // Map API response objects to Media3 MediaItems
        val mediaItems = songsToMediaList(songs)
        // Add MediaItems to the playlist and prepare the player
        player.addMediaItems(mediaItems)
        player.prepare()
        // Autoplay the next song
        player.playWhenReady = true
        updateUiMediaList(mediaItems)
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

    private fun updateUiMediaList(mediaItems: List<MediaItem>) {
        Log.d(SCREEN_NAME, "Updating media item list for UI recomposition")
        val uiState = _uiStateFlow.value
        _uiStateFlow.value = uiState.copy(
            isPlaying = true, // always auto-play next song
            metadataList = uiState.metadataList + mediaItems.map { it.mediaMetadata }
        )
    }

    private companion object {
        const val SCREEN_NAME = "PlaybackViewModel"
        const val SONGS_PER_PAGE = 10
    }
}
