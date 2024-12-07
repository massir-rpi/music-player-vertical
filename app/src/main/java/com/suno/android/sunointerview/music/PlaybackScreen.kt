package com.suno.android.sunointerview.music

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.suno.android.sunointerview.R
import com.suno.android.sunointerview.ui.theme.SunoInterviewTheme

@Composable
fun PlaybackScreen(
    navController: NavController,
    viewModel: PlaybackViewModel = hiltViewModel(),
) {
    Log.i("PlaybackScreen", "Entered playback screen")
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.setPlayer(buildPlayer(context))
    }

    Screen(
        uiState = uiState,
        onPlayingChanged = { viewModel.togglePlaying() },
    )
}

@Composable
private fun Screen(
    uiState: PlaybackUiState,
    onPlayingChanged: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.then(
            Modifier.fillMaxSize()
        ),
    ) { innerPadding ->
        MediaPlayer(
            uiState = uiState,
            onPlayingChanged = onPlayingChanged,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Preview
@Composable
fun PlaybackScreenPreview() {
    SunoInterviewTheme {
        Screen(
            uiState = PlaybackUiState(
                isPlaying = true,
                metadata = MediaMetadata.Builder()
                    .setTitle("Phonk (Trumpet)")
                    .setArtworkUri(Uri.parse("https://cdn1.suno.ai/image_1f534a9e-7ea4-4785-9bcf-f0b96731eead.png"))
                    .setArtist("More Phonk")
                    .setDescription("fast aggressive phonk, trumpet")
                    .build(),
            ),
            onPlayingChanged = {},
        )
    }
}

@Composable
private fun MediaPlayer(
    uiState: PlaybackUiState,
    onPlayingChanged: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (imageRef, titleRef, controlsRef) = createRefs()
        val titleString = uiState.metadata?.title?.let { remember { it.toString() } }

        uiState.metadata?.artworkUri?.let { imageUri ->
            AsyncImage(
                model = run {
                    val context = LocalContext.current
                    remember {
                        ImageRequest.Builder(context = context)
                            .data(imageUri.also { Log.i("PlaybackScreen", "Loading image from ${imageUri.path}") })
                            .build()
                    }
                },
                contentDescription = titleString,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(imageRef) {
                        linkTo(
                            start = parent.start,
                            top = parent.top,
                            end = parent.end,
                            bottom = parent.bottom,
                        )
                    },
            )
        }

        titleString?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .constrainAs(titleRef) {
                        start.linkTo(parent.start)
                        bottom.linkTo(controlsRef.top)
                    }
            )
        }

        MediaControls(
            playing = uiState.isPlaying,
            onPlayingChanged = onPlayingChanged,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(controlsRef) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                    )
                    bottom.linkTo(parent.bottom)
                },
        )
    }
}

@Composable
private fun MediaControls(
    playing: Boolean,
    onPlayingChanged: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        IconToggleButton(
            checked = playing,
            onCheckedChange = onPlayingChanged,
        ) {
            if (playing) {
                Icon(
                    painter = painterResource(R.drawable.pause),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(R.string.pause),
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.play_arrow),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(R.string.play),
                )
            }
        }
    }
}

private fun buildPlayer(context: Context) = ExoPlayer.Builder(context).build().apply {
    // video will not be rendered since this is audio playback
    setVideoSurface(null)
}
