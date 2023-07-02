package com.example.musicplayer.ui

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.example.musicplayer.model.Music
import com.example.musicplayer.util.FormatDuration

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false

    companion object {
        var musicListPA: ArrayList<Music> = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        musicListPA.clear()
        musicListPA.addAll(MainActivity.musicList)
        val position = intent.getIntExtra("index", 0)
        val songPath = musicListPA.get(position).path
        val songTitle = musicListPA.get(position).title
        val songAlbum = musicListPA.get(position).album
        val endTime = musicListPA.get(position).duration.FormatDuration()

        Glide.with(this)
            .load(songPath)
            .apply(RequestOptions.placeholderOf(R.drawable.ic_music)).centerCrop()
            .into(binding.ivSong)


        binding.tvSongName.text = songTitle

        binding.tvEndTime.text = endTime
        binding.tvSongAlbum.text = songAlbum

        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                mediaPlayer = MediaPlayer()
                mediaPlayer.reset()
                mediaPlayer.setDataSource(songPath)
                mediaPlayer.prepare()
                mediaPlayer.start()
                isPlaying = true
            }
        }
        binding.fabPlay.setOnClickListener {
            if (isPlaying) setPauseButton()
            else setPlayButton()
        }
    }

    private fun setPlayButton() {
        mediaPlayer.start()
        isPlaying = true
        binding.fabPlay.setIconResource(R.drawable.ic_pause)
    }

    private fun setPauseButton() {
        mediaPlayer.pause()
        isPlaying = false
        binding.fabPlay.setIconResource(R.drawable.ic_play)
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        super.onDestroy()
    }
}