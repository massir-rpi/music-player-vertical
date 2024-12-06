package com.suno.android.sunointerview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.suno.android.sunointerview.navigation.Screen
import com.suno.android.sunointerview.navigation.addPlaybackTopLevel
import com.suno.android.sunointerview.ui.theme.SunoInterviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            SunoInterviewTheme {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Playback.route,
                ) {
                    addPlaybackTopLevel(navController)
                }
            }
        }
    }
}
