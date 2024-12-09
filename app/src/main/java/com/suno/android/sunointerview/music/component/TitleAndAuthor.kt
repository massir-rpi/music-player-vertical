package com.suno.android.sunointerview.music.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
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

private const val CARD_SURFACE_OPACITY = 0.5F

@Composable
fun TitleAndAuthor(
    title: String?,
    avatarImageUri: Uri?,
    authorName: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier)  {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(
                        bottom = dimensionResource(R.dimen.small_padding),
                    ),
            )
        }

        AuthorCard(
            avatarImageUri = avatarImageUri,
            authorName = authorName,
            modifier = Modifier
                .wrapContentWidth(),
        )
    }
}

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
                    color = MaterialTheme.colorScheme.surface.copy(alpha = CARD_SURFACE_OPACITY),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.small_corner_clip))
                ),
        ),
    ) {
        AuthorAvatar(
            avatarImageUri = avatarImageUri,
            authorName = authorName,
            modifier = Modifier
                .size(dimensionResource(R.dimen.avatar_size))
                .padding(dimensionResource(R.dimen.tiny_padding)),
        )

        authorName?.let { name ->
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.tiny_padding)),
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
