package com.example.musicplayer.ui

import android.content.*
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.example.musicplayer.model.Music
import com.example.musicplayer.service.MusicService
import com.example.musicplayer.util.FormatDuration
import com.example.receiver.MusicBroadcastReceiver
import com.example.receiver.MusicControlListener
import kotlin.system.exitProcess

class PlayerActivity : AppCompatActivity(), ServiceConnection, MusicControlListener {

    private lateinit var binding: ActivityPlayerBinding
    private var isPlaying = MutableLiveData<Boolean>().apply {
        value = true
    }
    private val musicBroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.extras?.getString("actions")
            when (action) {
                MusicBroadcastReceiver.ACTION_PREVIOUS -> {
                    onPrevious()
                }
                MusicBroadcastReceiver.ACTION_NEXT -> {
                    onNext()
                }
                MusicBroadcastReceiver.ACTION_PLAY -> {
                    onPlay()
                }
                MusicBroadcastReceiver.ACTION_EXIT -> {
                    onExit()
                }
            }
        }

    }

    companion object {
        var musicListPA: ArrayList<Music> = arrayListOf()
        var position: Int = 0
        var musicService: MusicService? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(musicBroadcastReceiver, IntentFilter("TRACKS"))
        startsService()
        musicListPA.clear()
        musicListPA.addAll(MainActivity.musicList)
        position = intent.getIntExtra("index", 0)
        setLayout()


        isPlaying.observe(this) {
            if (it) binding.fabPlay.setIconResource(R.drawable.ic_pause)
            else binding.fabPlay.setIconResource(R.drawable.ic_play)
        }
        binding.fabPlay.setOnClickListener {
            if (isPlaying.value == true) setPauseButton()
            else setPlayButton()
        }

        binding.fabNext.setOnClickListener {
            setNextPreviousSong(true)
        }
        binding.fabPrevious.setOnClickListener {
            setNextPreviousSong(false)
        }
    }

    private fun startsService() {
        // bind and create service
        val serviceIntent = Intent(this, MusicService::class.java)
        bindService(serviceIntent, this, BIND_AUTO_CREATE)
        startService(serviceIntent)
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
    }

    private fun setMediaPlayer() {
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                if (musicService?.mediaPlayer == null) musicService?.mediaPlayer = MediaPlayer()
                musicService?.mediaPlayer?.reset()
                musicService?.mediaPlayer?.setDataSource(musicListPA.get(position).path)
                musicService?.mediaPlayer?.prepare()
                musicService?.mediaPlayer?.start()
                isPlaying.postValue(true)
            }
        }
    }

    private fun setPlayButton() {
        musicService?.mediaPlayer?.start()
        isPlaying.postValue(true)
        binding.fabPlay.setIconResource(R.drawable.ic_pause)
    }

    private fun setPauseButton() {
        musicService?.mediaPlayer?.pause()
        isPlaying.postValue(false)
        binding.fabPlay.setIconResource(R.drawable.ic_play)
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


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        setMediaPlayer()
        musicService?.showNotification(
            musicListPA.get(position).title,
            musicListPA.get(position).artist,
            true
        )
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onPlay() {
        if (isPlaying.value == true) setPauseButton()
        else setPlayButton()
        musicService?.showNotification(
            musicListPA.get(position).title,
            musicListPA.get(position).artist, !(isPlaying.value ?: false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onNext() {
        setNextPreviousSong(true)
        musicService?.showNotification(
            musicListPA.get(position).title,
            musicListPA.get(position).artist, true
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onPrevious() {
        setNextPreviousSong(false)
        musicService?.showNotification(
            musicListPA.get(position).title,
            musicListPA.get(position).artist,
            true
        )
    }

    override fun onExit() {
        exitProcess(1)
    }

    override fun onDestroy() {
        musicService?.mediaPlayer?.stop()
        unregisterReceiver(musicBroadcastReceiver)
        super.onDestroy()
    }

}