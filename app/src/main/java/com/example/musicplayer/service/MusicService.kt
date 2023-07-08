package com.example.musicplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.musicplayer.R
import com.example.musicplayer.util.Constants
import com.example.receiver.MusicBroadcastReceiver

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

    @RequiresApi(Build.VERSION_CODES.S)
    fun showNotification(title: String, artist: String, isPlaying: Boolean) {
        val prevIntent = Intent(
            baseContext,
            MusicBroadcastReceiver::class.java
        ).setAction(MusicBroadcastReceiver.ACTION_PREVIOUS)

        val prevPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, prevIntent, PendingIntent.FLAG_MUTABLE)

        val playIntent = Intent(
            baseContext,
            MusicBroadcastReceiver::class.java
        ).setAction(MusicBroadcastReceiver.ACTION_PLAY)
        val playPendingIntent =
            PendingIntent.getBroadcast(
                baseContext,
                0,
                playIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        val playPauseDrawable = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val nextIntent = Intent(
            baseContext,
            MusicBroadcastReceiver::class.java
        ).setAction(MusicBroadcastReceiver.ACTION_NEXT)
        val nextPendingIntent =
            PendingIntent.getBroadcast(
                baseContext,
                0,
                nextIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val exitIntent = Intent(
            baseContext,
            MusicBroadcastReceiver::class.java
        ).setAction(MusicBroadcastReceiver.ACTION_EXIT)
        val exitPendingIntent =
            PendingIntent.getBroadcast(
                baseContext,
                0,
                exitIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val notification =
            NotificationCompat.Builder(baseContext, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentText(title)
                .setContentTitle(artist)
                .setSmallIcon(R.drawable.ic_add_playlist)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_music))
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.sessionToken)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
//                .setShowWhen(false)
                .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent)
                .addAction(playPauseDrawable, "Play", playPendingIntent)
                .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
                .addAction(R.drawable.ic_exit, "Exit", exitPendingIntent)
                .build()
        startForeground(13, notification)
    }
}