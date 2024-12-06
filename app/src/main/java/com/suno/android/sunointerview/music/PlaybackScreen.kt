package com.suno.android.sunointerview.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun PlaybackScreen(
    navController: NavController,
    viewModel: PlaybackViewModel = hiltViewModel(),
) {
    viewModel.loadNextPage()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val response by viewModel.songsFlow.collectAsStateWithLifecycle()

        Text("${response?.songs?.first()?.title}", modifier = Modifier.padding(innerPadding))
    }
}
