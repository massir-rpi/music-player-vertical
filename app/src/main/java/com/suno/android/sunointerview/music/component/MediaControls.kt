package com.suno.android.sunointerview.music.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.suno.android.sunointerview.R
import kotlin.math.roundToInt

private const val MILLIS_IN_MINUTE = 60000
private const val MILLIS_IN_SECOND = 1000
private const val DEFAULT_DURATION = 240000F

@Composable
fun MediaControls(
    playing: Boolean,
    currentTimeMs: Float,
    duration: Long?,
    onTimeChange: ((Float) -> Unit),
    onTimeFinalized: (() -> Unit),
    onPlayingChanged: ((Boolean) -> Unit),
    onReplayTapped: (() -> Unit),
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.then(Modifier.fillMaxWidth()),
    ) {
        PlayPauseButton(
            playing = playing,
            onPlayingChanged = onPlayingChanged,
        )

        ReplayButton(
            onReplayTapped = onReplayTapped,
        )

        SeekBar(
            currentTimeMs = currentTimeMs,
            duration = duration,
            onTimeChange = onTimeChange,
            onTimeFinalized = onTimeFinalized,
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.std_padding))
                .fillMaxWidth(),
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

@Composable
private fun ReplayButton(
    onReplayTapped: (() -> Unit),
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onReplayTapped,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(R.drawable.replay),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = stringResource(R.string.replay),
        )
    }
}

@Composable
private fun SeekBar(
    currentTimeMs: Float,
    duration: Long?,
    onTimeChange: ((Float) -> Unit),
    onTimeFinalized: (() -> Unit),
    modifier: Modifier = Modifier,
) {
    val timeInt = currentTimeMs.roundToInt()
    val minutes = timeInt / MILLIS_IN_MINUTE
    val seconds = (timeInt - minutes * MILLIS_IN_MINUTE) / MILLIS_IN_SECOND

    ConstraintLayout(modifier = modifier) {
        val (sliderRef, timeTextRef) = createRefs()

        Slider(
            value = currentTimeMs,
            onValueChange = onTimeChange,
            onValueChangeFinished = onTimeFinalized,
            valueRange = 0F..(duration?.toFloat() ?: DEFAULT_DURATION),
            modifier = Modifier
                .fillMaxWidth(0.75F)
                .constrainAs(sliderRef) {
                    centerVerticallyTo(parent)
                    linkTo(
                        start = parent.start,
                        end = timeTextRef.start,
                    )
                },
        )
        Text(
            text = "%02d:%02d".format(minutes, seconds),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .constrainAs(timeTextRef) {
                    centerVerticallyTo(parent)
                    end.linkTo(parent.end)
                },
        )
    }
}
