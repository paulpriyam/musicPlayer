package com.example.musicplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.PlayerActivity
import com.example.musicplayer.R
import com.example.musicplayer.databinding.LayoutItemSongsBinding
import com.example.musicplayer.model.Music
import com.example.musicplayer.util.FormatDuration

class MusicAdapter(private val context: Context) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Music>() {
            override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem == newItem
            }
        }
    }

    val asyncListDiffer = AsyncListDiffer<Music>(this, diffUtil)

    inner class MusicViewHolder(private val binding: LayoutItemSongsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(music: Music) {
            binding.tvSongName.text = music.title
            binding.tvSongAlbum.text = music.album
            binding.tvSongDuration.text = music.duration.FormatDuration()
            Glide.with(context)
                .load(music.path)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_music)).centerCrop()
                .into(binding.ivSong)

            binding.root.setOnClickListener {
                val intent = Intent(context, PlayerActivity::class.java)
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(
            LayoutItemSongsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music = asyncListDiffer.currentList[position]
        holder.bind(music)
    }

}