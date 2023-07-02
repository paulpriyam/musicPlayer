package com.example.musicplayer.ui

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val songPath = intent.getStringExtra("path")
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                mediaPlayer = MediaPlayer()
                mediaPlayer.reset()
                mediaPlayer.setDataSource(songPath)
                mediaPlayer.prepare()
                mediaPlayer.start()

            }
        }
    }
}