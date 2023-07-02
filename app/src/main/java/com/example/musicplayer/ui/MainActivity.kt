package com.example.musicplayer.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.adapter.MusicAdapter
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.model.Music
import com.example.musicplayer.viewmodel.MusicViewModel
import com.google.android.material.navigation.NavigationView
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var viewModel: MusicViewModel

    companion object {
        const val EXTERNAL_STORAGE_REQUEST_CODE = 13
        var musicList: ArrayList<Music> = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setting up actionBatToggle
        viewModel = ViewModelProvider(this).get(MusicViewModel::class.java)
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener(this)
        musicAdapter = MusicAdapter(this)
        binding.rvSongs.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = musicAdapter
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions.entries.all { it.value }
                if (granted) {
                    getAllAudioFiles()
                } else {
                    Toast.makeText(this, "permission denied ", Toast.LENGTH_SHORT).show()
                }

            }

        if (checkStoragePermission()) {
            getAllAudioFiles()
        } else {
            getStoragePermission()
        }
        viewModel.musicList.observe(this) {
            musicAdapter.asyncListDiffer.submitList(it)
            musicList.addAll(it)
            binding.tvTotalSongs.text =
                "Total Songs: " + musicAdapter.asyncListDiffer.currentList.size.toString()
        }
    }

    private fun getStoragePermission() {
        val permission = arrayOf(
            android.Manifest.permission.READ_MEDIA_AUDIO
        )
        requestPermissionLauncher.launch(permission)
    }

    private fun checkStoragePermission(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return readPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onSupportNavigateUp(): Boolean {
        binding.root.openDrawer(binding.navView)
        return true
    }


    override fun onBackPressed() {
        if (binding.root.isDrawerOpen(GravityCompat.START)) {
            binding.root.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()

            }
            R.id.exit -> {
                Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show()

            }
            R.id.feedback -> {
                Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()

            }
            R.id.settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()

            }
        }
        binding.root.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("Range")
    private fun getAllAudioFiles() {
        val tempList = arrayListOf<Music>()

        val selection = MediaStore.Audio.Media.IS_MUSIC + " !=0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val dateAddedC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))
                    val albumIdC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val albumArtUri = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music =
                        Music(
                            idC,
                            titleC,
                            pathC,
                            albumC,
                            durationC,
                            artistC,
                            dateAddedC,
                            albumArtUri
                        )
                    val file = File(music.path)
                    if (file.exists()) {
                        tempList.add(music)
                    }
                    Log.d("--->", music.toString())
                } while (cursor.moveToNext())
                cursor.close()
            }
        } else {
            Log.d("--->", "cursor is null")
        }
        viewModel.musicList.postValue(tempList)
    }
}