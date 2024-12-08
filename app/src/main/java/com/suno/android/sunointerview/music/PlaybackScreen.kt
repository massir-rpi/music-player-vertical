package com.suno.android.sunointerview.music

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
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
import kotlin.math.roundToInt

private const val SCREEN_NAME = "PlaybackScreen"
private const val PLAYER_HEIGHT = 0.875F
private val sevenEighthsPageSize = object : PageSize {
    override fun Density.calculateMainAxisPageSize(
        availableSpace: Int,
        pageSpacing: Int,
    ): Int {
        return ((availableSpace - 2 * pageSpacing) * PLAYER_HEIGHT).roundToInt()
    }
}

@Composable
fun PlaybackScreen(
    navController: NavController,
    viewModel: PlaybackViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.i(SCREEN_NAME, "Entered $SCREEN_NAME and building player for view model")
        viewModel.setPlayer(buildPlayer(context))
    }

    Screen(
        uiState = uiState,
        onPlayingChanged = { viewModel.togglePlaying() },
        onPageSelected = { viewModel.seekToMediaItem(it) },
        getNumSongs = { viewModel.numSongs },
    )
}

@Preview
@Composable
fun PlaybackScreenPreview() {
    SunoInterviewTheme {
        Screen(
            uiState = PlaybackUiState(
                isPlaying = true,
                metadataList = listOf(
                    MediaMetadata.Builder()
                        .setTitle("I Spent 3000 Credits on This Song")
                        .setArtworkUri(Uri.parse("https://cdn1.suno.ai/ffa48fbf-ac87-4a02-8cf2-f3766f518d58_c134aeb8.png"))
                        .setArtist("nanashi_zero")
                        .setDescription("Catchy Instrumental intro. electro swing. sweet female vocal, witch house")
                        .build(),
                    MediaMetadata.Builder()
                        .setTitle("Chemical Elements")
                        .setArtworkUri(Uri.parse("https://cdn1.suno.ai/5f324463-08a7-490e-b9c5-f8e2d399baa9_4fba4ab7.png"))
                        .setArtist("nanashi_zero")
                        .setDescription("Catchy Instrumental intro. opera. fire. darkjazz")
                        .build(),
                    MediaMetadata.Builder()
                        .setTitle("Magical Potion [SSC3, Australia]")
                        .setArtworkUri(Uri.parse("https://cdn1.suno.ai/40e5fbba-e780-46ff-8ec6-4308ec05dad4_2569e084.png"))
                        .setArtist("nanashi_zero")
                        .setDescription("Catchy Instrumental intro. [electro swing- witch house]. sweet female vocal, [witch house]")
                        .build(),
                ),
            ),
            onPlayingChanged = {},
            onPageSelected = {},
            getNumSongs = { 10 },
        )
    }
}

@Composable
private fun Screen(
    uiState: PlaybackUiState,
    onPlayingChanged: ((Boolean) -> Unit),
    onPageSelected: ((Int) -> Unit),
    getNumSongs: (() -> Int),
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.then(
            Modifier.fillMaxSize()
        ),
    ) { innerPadding ->
        val pagerState = rememberPagerState { getNumSongs() }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                onPageSelected(page)
            }
        }

        VerticalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            pageSize = sevenEighthsPageSize,
            pageSpacing = dimensionResource(R.dimen.page_spacing),
            contentPadding = PaddingValues(dimensionResource(R.dimen.page_padding)),
            snapPosition = SnapPosition.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            MediaPlayer(
                metadata = uiState.metadataList[it],
                isPlaying = uiState.isPlaying,
                onPlayingChanged = onPlayingChanged,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun MediaPlayer(
    metadata: MediaMetadata,
    isPlaying: Boolean,
    onPlayingChanged: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(modifier = modifier) {
        val (imageRef, titleRef, controlsRef) = createRefs()
        val titleString = metadata.title?.let { remember { it.toString() } }

        metadata.artworkUri?.let { imageUri ->
            AsyncImage(
                model = run {
                    val context = LocalContext.current
                    remember {
                        Log.d(SCREEN_NAME, "Loading image from ${imageUri.path}")
                        ImageRequest.Builder(context = context)
                            .data(imageUri)
                            .build()
                    }
                },
                contentDescription = titleString,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_clip)))
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
                    },
            )
        }

        MediaControls(
            playing = isPlaying,
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
        PlayPauseButton(
            playing = playing,
            onPlayingChanged = onPlayingChanged,
        )
    }
}

@Composable
private fun PlayPauseButton(
    playing: Boolean,
    onPlayingChanged: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    IconToggleButton(
        checked = playing,
        onCheckedChange = onPlayingChanged,
        modifier = modifier,
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

private fun buildPlayer(context: Context) = ExoPlayer.Builder(context).build().apply {
    // video will not be rendered since this is audio playback
    setVideoSurface(null)
}
