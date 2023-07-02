package com.example.musicplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.model.Music

class MusicViewModel : ViewModel() {

    private var _musicList = MutableLiveData<List<Music>>()
    val musicList get() = _musicList
}