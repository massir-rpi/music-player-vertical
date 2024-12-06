package com.suno.android.sunointerview.navigation

sealed class Destination(val route: String) {
    data object MainNavGraph: Destination(route = "mainNavGraph")
    data object Playback: Destination(route = "playback")
}
