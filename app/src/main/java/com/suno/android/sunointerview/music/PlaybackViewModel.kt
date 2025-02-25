package com.suno.android.sunointerview.music

import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.suno.android.sunointerview.api.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val future: ListenableFuture<MediaController>,
) : ViewModel() {
    private val _uiStateFlow = MutableStateFlow(PlaybackUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private var player: Player? = null

    private var numPages = 0

    // Lock to prevent the player from updating the slider value while the user is changing the value
    private var sliderLock = false
    // Keep track of job updating time to ensure only one is ever running
    private var updateTimeJob: Job? = null

    init {
        future.addListener(
            { setPlayer(future.get()) },
            MoreExecutors.directExecutor(),
        )
    }

    private fun setPlayer(player: Player?) {
        Log.d(SCREEN_NAME, "Setting player in view model")
        this.player = player

        player?.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    updateCurrentTime()
                }
            }
        )
    }

    fun onTracksChanged(onCurrentMediaChanged: ((Int) -> Unit)) {
        player?.let {
            it.addListener(
                object : Player.Listener {
                    override fun onTracksChanged(tracks: Tracks) {
                        super.onTracksChanged(tracks)
                        onCurrentMediaChanged(it.currentMediaItemIndex)
                    }
                }
            )
        }
    }

    fun setPlaying(playing: Boolean) {
        player?.playWhenReady = playing
        _uiStateFlow.value = _uiStateFlow.value.copy(
            isPlaying = playing,
        )
    }

    fun setLiked(isLiked: Boolean) {
        val uiState = _uiStateFlow.value
        player?.currentMediaItemIndex?.let { index ->
            val updatedMetadataList = uiState.metadataList.subList(0, index) +
                    uiState.metadataList[index].copy(
                        isLiked = isLiked,
                        isDisliked = false,
                    ) +
                    uiState.metadataList.subList(index + 1, uiState.metadataList.size)
            _uiStateFlow.value = uiState.copy(
                metadataList = updatedMetadataList
            )
        }
    }

    fun setDisliked(isDisliked: Boolean) {
        val uiState = _uiStateFlow.value
        player?.currentMediaItemIndex?.let { index ->
            val updatedMetadataList = uiState.metadataList.subList(0, index) +
                    uiState.metadataList[index].copy(
                        isLiked = false,
                        isDisliked = isDisliked,
                    ) +
                    uiState.metadataList.subList(index + 1, uiState.metadataList.size)
            _uiStateFlow.value = uiState.copy(
                metadataList = updatedMetadataList
            )
        }
    }

    fun onSliderDrag(timeMs: Float) {
        sliderLock = true
        updateUiCurrentTime(timeMs.toLong())
    }

    fun seekToSlider() {
        player?.seekTo(_uiStateFlow.value.currentTime.toLong())
        sliderLock = false
    }

    fun seekToStart() {
        player?.seekTo(0L)
    }

    fun seekToMediaItem(index: Int) {
        // Load the next page of songs if we're near the end of the current page
        Log.d(SCREEN_NAME, "View model seekToMediaItem called with index $index on playlist of size ${player?.mediaItemCount}")
        if (player?.mediaItemCount?.let {index >= it - 1} != false) {
            loadNextPage(index)
        } else {
            // Seek to the song at the given index
            Log.d(SCREEN_NAME, "Seeking to $index in playlist of size ${player?.mediaItemCount}")
            player?.seekTo(index, C.TIME_UNSET)
            // Always auto-play next song
            if (!uiStateFlow.value.isPlaying) {
                setPlaying(true)
            }
            Log.d(SCREEN_NAME, "num pages: ${uiStateFlow.value.metadataList.size}")
        }
    }

    private fun loadNextPage(indexSeekTo: Int) {
        viewModelScope.launch {
            Log.d(SCREEN_NAME, "Loading next page ($numPages)")
            musicRepository.getSongs(numPages, SONGS_PER_PAGE).body()?.songs?.let {
                numPages += 1
                loadMediaIntoPlayer(it)
                seekToMediaItem(indexSeekTo)
            }
        }
    }

    private fun loadMediaIntoPlayer(songs: List<Song?>) {
        Log.d(SCREEN_NAME, "Adding page to media list and preparing for playback")
        // Map API response objects to Media3 MediaItems and UI consumable SongMetadata
        val mediaList = songsToMediaList(songs)
        // Add listener to future so we can queue songs before player initializes
        future.addListener(
            {
                future.get().apply {
                    // Add MediaItems to the playlist and prepare the player
                    addMediaItems(mediaList.map { it.first })
                    prepare()
                    // Autoplay the next song
                    playWhenReady = true
                }
            },
            MoreExecutors.directExecutor(),
        )
        // Make metadata available to UI
        updateUiMediaList(mediaList.map { it.second })
    }

    @OptIn(UnstableApi::class)
    private fun songsToMediaList(songs: List<Song?>) = songs.mapNotNull { songNullable ->
        songNullable?.let { song ->
            MediaItem.Builder().apply {
                setUri(song.audioUrl)
                song.id?.let { setMediaId(it) }
                setMediaMetadata(
                    MediaMetadata.Builder().apply {
                        song.title?.let { setTitle(it) }
                        song.imageUrl?.let { setArtworkUri(Uri.parse(it)) }
                        song.handle?.let { setArtist(it) }
                        song.metadata?.tags?.let { setDescription(it) }
                        song.metadata?.duration?.let { setDurationMs((it * MILLIS_IN_SECOND).toLong()) }
                    }.build()
                )
            }.build() to
            SongMetadata(
                avatarImageUri = song.avatarImageUrl?.let { Uri.parse(it) },
                authorName = song.handle,
                durationMs = song.metadata?.duration?.let { (it * MILLIS_IN_SECOND).toFloat() },
                imageUri = song.imageUrl?.let { Uri.parse(it) },
                isLiked = song.isLiked ?: false,
                isDisliked = song.isTrashed ?: false,
                title = song.title,
                upvoteCount = song.upvoteCount,
                shareUrl = song.videoUrl,
            )
        }
    }

    private fun updateUiMediaList(metadataList: List<SongMetadata>) {
        Log.d(SCREEN_NAME, "Updating media item list for UI recomposition")
        val uiState = _uiStateFlow.value
        _uiStateFlow.value = uiState.copy(
            isPlaying = true, // always auto-play next song
            metadataList = uiState.metadataList + metadataList,
        )
    }

    private fun updateUiCurrentTime(timeMs: Long?) {
        val uiState = _uiStateFlow.value
        timeMs?.toFloat()?.let {
            _uiStateFlow.value = uiState.copy(
                currentTime = it,
            )
        }
    }

    private fun updateCurrentTime() {
        // Set current time to the player's position if the user isn't dragging the seek bar
        if (!sliderLock) {
            updateUiCurrentTime(player?.currentPosition)
        }

        // If playing, update time again in one second
        val playerState = player?.playbackState
        if (playerState != Player.STATE_IDLE && playerState != Player.STATE_ENDED) {
            // If there's already a timer running, cancel it
            if (updateTimeJob?.isActive == true) {
                updateTimeJob!!.cancel()
            }
            updateTimeJob = viewModelScope.launch {
                delay(MILLIS_IN_SECOND)
                updateCurrentTime()
            }
        }
    }

    private companion object {
        const val SCREEN_NAME = "PlaybackViewModel"
        const val SONGS_PER_PAGE = 10
        const val MILLIS_IN_SECOND = 1000L
    }
}
