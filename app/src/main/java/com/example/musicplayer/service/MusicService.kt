package com.example.musicplayer.service

import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.musicplayer.R
import com.example.musicplayer.util.Constants

class MusicService : Service() {

    private var binder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My music")
        return binder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(title: String, artist: String) {
        val notification =
            NotificationCompat.Builder(baseContext, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentText(title)
                .setContentTitle(artist)
                .setSmallIcon(R.drawable.ic_add_playlist)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_music))
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_previous, "Previous", null)
                .addAction(R.drawable.ic_play, "Play", null)
                .addAction(R.drawable.ic_next, "Next", null)
                .addAction(R.drawable.ic_exit, "Exit", null)
                .build()
        startForeground(13, notification)
    }
}