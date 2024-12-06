package com.suno.android.sunointerview.navigation

sealed class Screen(val route: String) {
    data object Playback: Screen(route = "playback")
}
