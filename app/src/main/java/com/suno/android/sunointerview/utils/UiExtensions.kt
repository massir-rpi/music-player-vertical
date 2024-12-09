package com.suno.android.sunointerview.utils

import androidx.compose.ui.graphics.Color

fun Color.darken(ratio: Float = 0.75F): Color = copy(
    alpha = alpha,
    red = red * ratio,
    green = green * ratio,
    blue = blue * ratio,
)
