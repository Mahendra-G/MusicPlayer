package com.example.musicplayer.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.adapter.MusicPlayerAdapter
import com.example.musicplayer.model.checkPlaylist
import com.example.musicplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: MusicPlayerAdapter

    companion object {
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeSharedData()
        initializeBindingsListeners()
        initializeRecyclerView()
        changeStatusBarColor()
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.teal_200)
        }
    }

    private fun initializeSharedData() {
        currentPlaylistPos = intent.extras?.get("index") as Int
        try {
            PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist =
                checkPlaylist(playlist = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist)
        } catch (e: Exception) {
            return
        }
    }

    private fun initializeRecyclerView() {
        binding.playlistDetailsRv.setHasFixedSize(true)
        binding.playlistDetailsRv.setItemViewCacheSize(20)
        binding.playlistDetailsRv.layoutManager = LinearLayoutManager(this)
        adapter = MusicPlayerAdapter(
            this,
            PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist, playlistDetails = true
        )
        binding.playlistDetailsRv.adapter = adapter
    }

    private fun initializeBindingsListeners() {
        binding.backBtnPD.setOnClickListener { finish() }

        binding.addSongBtn.setOnClickListener {
            startActivity(Intent(this, SongSelectionActivity::class.java))
        }
        binding.removeSongBtn.setOnClickListener {
            openAlertDialog()
        }
    }

    private fun openAlertDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Remove")
            .setMessage("Do you want to remove all song ?")
            .setPositiveButton("Yes") { dialog, _ ->
                PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                adapter.refreshPlaylist()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val customDialog = builder.create()
        customDialog.show()
        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
    }


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.tvPlalistNamePD.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.tvMoreInfo.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created By -\n${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if (adapter.itemCount > 0) {
            Glide.with(this)
                .load(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon)).centerCrop()
                .into(binding.playlistImgPD)

        }
        adapter.notifyDataSetChanged()

    }
}