package com.suno.android.sunointerview.music.component

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AuthorCard(
    avatarImageUrl: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        AuthorAvatar()
    }
}

@Composable
private fun AuthorAvatar(
    modifier: Modifier = Modifier
) {

}
