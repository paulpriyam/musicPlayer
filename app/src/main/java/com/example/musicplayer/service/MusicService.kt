package com.example.musicplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {

    private var binder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }
}