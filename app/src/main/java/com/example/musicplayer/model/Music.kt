package com.example.musicplayer.model

data class Music(
    val id:String,
    val title:String,
    val path:String,
    val album:String,
    val duration:Long,
    val artist:String,
    val addedDate:Long,
    val artUri:String
)
