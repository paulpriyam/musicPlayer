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
        var position: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        musicListPA.clear()
        musicListPA.addAll(MainActivity.musicList)
        position = intent.getIntExtra("index", 0)
        mediaPlayer = MediaPlayer()
        setLayout()
        setMediaPlayer()



        binding.fabPlay.setOnClickListener {
            if (isPlaying) setPauseButton()
            else setPlayButton()
        }

        binding.fabNext.setOnClickListener {
            setNextPreviousSong(true)
        }
        binding.fabPrevious.setOnClickListener {
            setNextPreviousSong(false)
        }
    }

    private fun setLayout() {
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
        if(isPlaying) binding.fabPlay.setIconResource(R.drawable.ic_pause)
        else binding.fabPlay.setIconResource(R.drawable.ic_play)
    }

    private fun setMediaPlayer() {
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(musicListPA.get(position).path)
                mediaPlayer.prepare()
                mediaPlayer.start()
                isPlaying = true
            }
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

    private fun setNextPreviousSong(increase: Boolean) {
        if (increase) {
            setSongPosition(true)
        } else {
            setSongPosition(false)
        }
        setMediaPlayer()
        setLayout()
    }

    private fun setSongPosition(increment: Boolean) {
        if (increment) {
            if (musicListPA.size - 1 == position) position = 0 else ++position
        } else {
            if (position == 0) position = musicListPA.size - 1 else --position
        }
    }
}