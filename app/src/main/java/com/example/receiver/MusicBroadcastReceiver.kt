package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val sendingIntent = Intent("TRACKS")
            .putExtra("actions", intent?.action)
        context?.sendBroadcast(sendingIntent)
    }

    companion object {
        const val ACTION_PLAY = "com.example.musicplayer.ACTION_PLAY"
        const val ACTION_NEXT = "com.example.musicplayer.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.musicplayer.ACTION_PREVIOUS"
        const val ACTION_EXIT = "com.example.musicplayer.ACTION_EXIT"
    }
}

interface MusicControlListener {
    fun onPlay()
    fun onNext()
    fun onPrevious()
    fun onExit()
}