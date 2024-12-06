package com.suno.android.sunointerview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.suno.android.sunointerview.music.PlaybackScreen

fun NavGraphBuilder.addPlaybackTopLevel(
    navController: NavController
) {
    navigation(
        route = Screen.Playback.route,
        startDestination = Screen.Playback.route
    ) {
        composable(Screen.Playback.route) {
            PlaybackScreen(navController = navController)
        }
    }
}
