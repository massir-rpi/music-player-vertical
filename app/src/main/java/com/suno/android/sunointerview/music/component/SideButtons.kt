package com.suno.android.sunointerview.music.component

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.suno.android.sunointerview.R

private const val BUTTON_SURFACE_OPACITY = 0.5F
private const val SELECTED_BUTTON_SURFACE_OPACITY = 0.75F

@Composable
fun SideButtons(
    upvoteCount: Int?,
    shareUrl: String?,
    isLiked: Boolean,
    isDisliked: Boolean,
    onLikeTapped: ((Boolean) -> Unit),
    onDislikeTapped: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.then(
            // Panel for side buttons should be scrollable for accessibility in case screen is too short to fit them
            Modifier.verticalScroll(rememberScrollState()),
        ),
    ) {
        val buttonModifier = Modifier
            .padding(vertical = dimensionResource(R.dimen.tiny_padding))
            .size(dimensionResource(R.dimen.side_button_size))

        LikeButton(
            upvoteCount = upvoteCount,
            isLiked = isLiked,
            onLikeTapped = onLikeTapped,
            modifier = buttonModifier,
        )

        DislikeButton(
            isDisliked = isDisliked,
            onDislikeTapped = onDislikeTapped,
            modifier = buttonModifier,
        )

        ShareButton(
            shareUrl = shareUrl,
            modifier = buttonModifier,
        )

        MoreButton(
            modifier = buttonModifier,
        )
    }
}

@Composable
private fun LikeButton(
    upvoteCount: Int?,
    isLiked: Boolean,
    onLikeTapped: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    IconToggleButton(
        checked = isLiked,
        onCheckedChange = onLikeTapped,
        modifier = modifier.then(
            Modifier
                .background(
                    color =  if (isLiked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = SELECTED_BUTTON_SURFACE_OPACITY) else MaterialTheme.colorScheme.surface.copy(alpha = BUTTON_SURFACE_OPACITY),
                    shape = CircleShape,
                ),
        ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.thumb_up),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = stringResource(R.string.like),
            )
            upvoteCount?.let { numLikes ->
                Text(
                    text = numToStrAbbreviate(numLikes),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun DislikeButton(
    isDisliked: Boolean,
    onDislikeTapped: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    IconToggleButton(
        checked = isDisliked,
        onCheckedChange = onDislikeTapped,
        modifier = modifier.then(
            Modifier
                .background(
                    color = (if (isDisliked) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface).copy(alpha = BUTTON_SURFACE_OPACITY),
                    shape = CircleShape,
                ),
        ),
    ) {
        Icon(
            painter = painterResource(R.drawable.thumb_down),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(R.string.dislike),
        )
    }
}

@Composable
private fun ShareButton(
    shareUrl: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    IconButton(
        onClick = { shareUrl?.let { shareUrl(context, it) } },
        modifier = modifier.then(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = BUTTON_SURFACE_OPACITY),
                    shape = CircleShape,
                ),
        ),
    ) {
        Icon(
            painter = painterResource(R.drawable.share),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(R.string.share),
        )
    }
}

@Composable
private fun MoreButton(
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = {},
        modifier = modifier.then(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = BUTTON_SURFACE_OPACITY),
                    shape = CircleShape,
                ),
        ),
    ) {
        Icon(
            painter = painterResource(R.drawable.more),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(R.string.more),
        )
    }
}

private fun numToStrAbbreviate(num: Int) =
    if (num > 999999999) {
        "${num / 1000000000}B"
    } else if (num > 999999) {
        "${num / 1000000}M"
    } else if (num > 999) {
        "${num / 1000}K"
    } else {
        num.toString()
    }

private fun shareUrl(context: Context, shareUrl: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_body, shareUrl))
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}
