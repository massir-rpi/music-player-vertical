package com.suno.android.sunointerview.music

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.suno.android.sunointerview.R
import com.suno.android.sunointerview.music.component.MediaControls
import com.suno.android.sunointerview.music.component.SideButtons
import com.suno.android.sunointerview.music.component.TitleAndAuthor
import com.suno.android.sunointerview.ui.theme.SunoInterviewTheme
import com.suno.android.sunointerview.utils.darken
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val SCREEN_NAME = "PlaybackScreen"
private const val PLAYER_HEIGHT_RATIO = 0.925F
private const val PAGE_SCROLL_OFFSET = (1 - 1 / PLAYER_HEIGHT_RATIO) / 2

private val fractionalPageSize = object : PageSize {
    override fun Density.calculateMainAxisPageSize(
        availableSpace: Int,
        pageSpacing: Int,
    ): Int {
        return ((availableSpace - 2 * pageSpacing) * PLAYER_HEIGHT_RATIO).roundToInt()
    }
}

@Composable
fun PlaybackScreen(
    navController: NavController,
    viewModel: PlaybackViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val pagerState = rememberPagerState { viewModel.numSongs }
    val coroutineScope = rememberCoroutineScope()

    // Build ExoPlayer
    LaunchedEffect(Unit) {
        Log.i(SCREEN_NAME, "Entered $SCREEN_NAME and building player for view model")
        val player = buildPlayer(context) {
            coroutineScope.launch {
                // Scroll pager state when player autoplays the next song
                if (pagerState.currentPage != it) {
                    Log.d(SCREEN_NAME, "Autoplaying and scrolling to song $it out of ${uiState.metadataList.size}")
                    pagerState.animateScrollToPage(
                        page = it,
                        pageOffsetFraction = PAGE_SCROLL_OFFSET,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow,
                        ),
                    )
                }
            }
        }
        viewModel.reset()
        viewModel.setPlayer(player)

        // Prevent leakage by tracking playback to view lifecycle
        lifecycleOwner.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    viewModel.setPlaying(false)
                    super.onStop(owner)
                }
                override fun onDestroy(owner: LifecycleOwner) {
                    player.release()
                    super.onDestroy(owner)
                }

                override fun onResume(owner: LifecycleOwner) {
                    super.onResume(owner)
                    viewModel.setPlaying(true)
                }
            }
        )
    }

    // Seek to song in playlist when scrolling through pager
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            Log.d(SCREEN_NAME, "Selecting song $page out of ${uiState.metadataList.size}")
            viewModel.seekToMediaItem(page)
        }
    }


    Screen(
        uiState = uiState,
        pagerState = pagerState,
        onPlayingChanged = { viewModel.setPlaying(it) },
        onReplayTapped = { viewModel.seekToStart() },
        onTimeChange = { viewModel.onSliderDrag(it) },
        onTimeFinalized = { viewModel.seekToSlider() },
        onLikeTapped = { viewModel.setLiked(it) },
        onDislikeTapped = { viewModel.setDisliked(it) },
    )
}

@Preview
@Composable
fun PlaybackScreenPreview() {
    SunoInterviewTheme {
        Screen(
            uiState = PlaybackUiState(
                isPlaying = true,
                currentTime = 152000F,
                metadataList = listOf(
                    SongMetadata(
                        title = "I Spent 3000 Credits on This Song",
                        imageUri = Uri.parse("https://cdn1.suno.ai/ffa48fbf-ac87-4a02-8cf2-f3766f518d58_c134aeb8.png"),
                        authorName = "nanashi_zero",
                        isLiked = true,
                        durationMs = 184920F,
                        upvoteCount = 6851,
                    ),
                    SongMetadata(
                        title = "Chemical Elements",
                        imageUri = Uri.parse("https://cdn1.suno.ai/5f324463-08a7-490e-b9c5-f8e2d399baa9_4fba4ab7.png"),
                        authorName = "nanashi_zero",
                        durationMs = 144960F,
                    ),
                    SongMetadata(
                        title = "Magical Potion [SSC3, Australia]",
                        imageUri = Uri.parse("https://cdn1.suno.ai/40e5fbba-e780-46ff-8ec6-4308ec05dad4_2569e084.png"),
                        authorName = "nanashi_zero",
                        durationMs = 192200F,
                    ),
                ),
            ),
            pagerState = rememberPagerState(1) { 10 },
            onPlayingChanged = {},
            onReplayTapped = {},
            onTimeChange = {},
            onTimeFinalized = {},
            onLikeTapped = {},
            onDislikeTapped = {},
        )
    }
}

@Composable
private fun Screen(
    uiState: PlaybackUiState,
    pagerState: PagerState,
    onPlayingChanged: ((Boolean) -> Unit),
    onReplayTapped: (() -> Unit),
    onTimeChange: ((Float) -> Unit),
    onTimeFinalized: (() -> Unit),
    onLikeTapped: ((Boolean) -> Unit),
    onDislikeTapped: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.then(
            Modifier.fillMaxSize()
        ),
    ) { innerPadding ->
        VerticalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            pageSize = fractionalPageSize,
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
                currentTimeMs = uiState.currentTime,
                onPlayingChanged = onPlayingChanged,
                onReplayTapped = onReplayTapped,
                onTimeChange = onTimeChange,
                onTimeFinalized = onTimeFinalized,
                onLikeTapped = onLikeTapped,
                onDislikeTapped = onDislikeTapped,
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
    }
}

@Composable
private fun MediaPlayer(
    metadata: SongMetadata,
    isPlaying: Boolean,
    currentTimeMs: Float,
    onTimeChange: ((Float) -> Unit),
    onTimeFinalized: (() -> Unit),
    onPlayingChanged: ((Boolean) -> Unit),
    onReplayTapped: (() -> Unit),
    onLikeTapped: ((Boolean) -> Unit),
    onDislikeTapped: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.then(
            Modifier.clip(RoundedCornerShape(dimensionResource(R.dimen.large_corner_clip))),
        ),
    ) {
        val titleString = metadata.title?.let { remember { it } }

        metadata.imageUri?.let { imageUri ->
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
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00F to Color.Transparent,
                            0.25F to MaterialTheme.colorScheme.primaryContainer.darken().copy(alpha = 0.625F),
                            1.00F to MaterialTheme.colorScheme.primaryContainer.darken().copy(alpha = 0.9375F),
                        ),
                    ),
                ),
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.medium_padding),
                        top = dimensionResource(R.dimen.medium_padding),
                        end = dimensionResource(R.dimen.small_padding),
                        bottom = dimensionResource(R.dimen.small_padding),
                    )
            ) {
                TitleAndAuthor(
                    title = titleString,
                    avatarImageUri = metadata.avatarImageUri,
                    authorName = metadata.authorName,
                    modifier = Modifier
                        .padding(
                            bottom = dimensionResource(R.dimen.large_padding)
                        )
                        .weight(1F),
                )

                SideButtons(
                    upvoteCount = metadata.upvoteCount,
                    shareUrl = metadata.shareUrl,
                    isLiked = metadata.isLiked,
                    isDisliked = metadata.isDisliked,
                    onLikeTapped = onLikeTapped,
                    onDislikeTapped = onDislikeTapped,
                    modifier = Modifier
                        .padding(start = dimensionResource(R.dimen.tiny_padding)),
                )
            }

            MediaControls(
                playing = isPlaying,
                currentTimeMs = currentTimeMs,
                duration = metadata.durationMs,
                onTimeChange = onTimeChange,
                onTimeFinalized = onTimeFinalized,
                onPlayingChanged = onPlayingChanged,
                onReplayTapped = onReplayTapped,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(R.dimen.large_padding)),
            )
        }
    }
}

private fun buildPlayer(context: Context, onCurrentMediaChanged: ((Int) -> Unit)) = ExoPlayer.Builder(context).build().apply {
    // Video will not be rendered since this is audio playback
    setVideoSurface(null)
    // Ensure we scroll to next page when automatically playing next song
    addListener(
        object : Player.Listener {
            override fun onTracksChanged(tracks: Tracks) {
                super.onTracksChanged(tracks)
                onCurrentMediaChanged(currentMediaItemIndex)
            }
        }
    )
}
