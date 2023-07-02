package com.example.musicplayer.util

import java.util.concurrent.TimeUnit

fun Long.FormatDuration(): String {
    val minutes = TimeUnit.MINUTES.convert(this, TimeUnit.MILLISECONDS)
    val seconds = TimeUnit.SECONDS.convert(
        this,
        TimeUnit.MILLISECONDS
    ) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MILLISECONDS)
    return String.format("%2d:%2d", minutes, seconds)
}