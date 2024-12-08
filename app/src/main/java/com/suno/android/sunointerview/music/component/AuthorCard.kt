package com.suno.android.sunointerview.music.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.suno.android.sunointerview.R

private const val CARD_OPACITY = 0.5F

@Composable
fun AuthorCard(
    avatarImageUri: Uri?,
    authorName: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.then(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = CARD_OPACITY),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.small_corner_clip))
                )
                .height(dimensionResource(R.dimen.avatar_size))
        ),
    ) {
        AuthorAvatar(
            avatarImageUri = avatarImageUri,
            authorName = authorName,
            modifier = Modifier
                .size(dimensionResource(R.dimen.avatar_size))
                .padding(dimensionResource(R.dimen.small_padding)),
        )

        authorName?.let { name ->
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.small_padding)),
            )
        }
    }
}

@Composable
private fun AuthorAvatar(
    avatarImageUri: Uri?,
    authorName: String?,
    modifier: Modifier = Modifier
) {
    avatarImageUri?.let { imageUri ->
        AsyncImage(
            model = run {
                val context = LocalContext.current
                remember {
                    ImageRequest.Builder(context)
                        .data(imageUri)
                        .build()
                }
            },
            contentDescription = authorName,
            contentScale = ContentScale.Crop,
            modifier = modifier.then(
                Modifier.clip(CircleShape),
            )
        )
    }
}
