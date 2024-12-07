package com.suno.android.sunointerview.music

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.suno.android.sunointerview.api.Song

@OptIn(UnstableApi::class)
@Composable
fun PlaybackScreen(
    navController: NavController,
    viewModel: PlaybackViewModel = hiltViewModel(),
) {
    viewModel.loadNextPage()
    val context = LocalContext.current
    val player = remember { buildPlayer(context) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val response by viewModel.songsFlow.collectAsStateWithLifecycle()

        response?.songs?.first()?.let { song -> loadMediaIntoPlayer(player, song) }
        MediaPlayer(
            exoPlayer = player,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun MediaPlayer(exoPlayer: ExoPlayer, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                // hideController()
                // useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                exoPlayer.playWhenReady = true
            }
        },
        modifier = modifier,
    )
}

@OptIn(UnstableApi::class)
private fun loadMediaIntoPlayer(exoPlayer: ExoPlayer, song: Song) {
    exoPlayer.apply {
        song.videoUrl?.let { setMediaItem(MediaItem.fromUri(it)) }
    }
}

private fun buildPlayer(context: Context) = ExoPlayer.Builder(context).build()
