package com.example.musicplayer.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.model.MusicPlaylist
import com.example.musicplayer.model.Playlist
import com.example.musicplayer.adapter.PlaylistAdapter
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.AddPlaylistDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistAdapter

    companion object {
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeRecyclerViewForMusic()
        initializeBindings()

    }

    private fun initializeBindings() {
        binding.addPlayListBtn.setOnClickListener {
            customAlertDialog()
        }
        binding.backBtnPLA.setOnClickListener { finish() }
    }

    private fun initializeRecyclerViewForMusic() {
        binding.playlistRv.setHasFixedSize(true)
        binding.playlistRv.setItemViewCacheSize(20)
        binding.playlistRv.layoutManager = GridLayoutManager(this, 2)
        adapter = PlaylistAdapter(this, playlist = musicPlaylist.ref)
        binding.playlistRv.adapter = adapter

    }

    private fun customAlertDialog() {
        val customDialog =
            LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist")
            .setPositiveButton("Add") { dialog, _ ->
                val playlistName = binder.tvPlaylistName.text
                val createdBy = binder.tvUserName.text
                if (playlistName != null && createdBy != null) {
                    if (playlistName.isNotEmpty() && createdBy.isNotEmpty()) {
                        addPlaylist(playlistName.toString(), createdBy.toString())
                    }
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun addPlaylist(name: String, createdBy: String) {
        var playlistExist = false
        for (i in musicPlaylist.ref) {
            if (name == i.name) {
                playlistExist = true
                break
            }
        }
        if (playlistExist) {
            Toast.makeText(this, "Playlist already exists", Toast.LENGTH_LONG).show()
        } else {
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calendar)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}