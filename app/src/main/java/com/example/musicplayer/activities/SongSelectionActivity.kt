package com.example.musicplayer.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.adapter.MusicPlayerAdapter
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivitySongSelectionBinding

class SongSelectionActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySongSelectionBinding
    private lateinit var adapter : MusicPlayerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeRecyclerView()
        initializeBindingListener()
        changeStatusBarColor()
    }

    private fun initializeRecyclerView() {
        binding.selectionRv.setHasFixedSize(true)
        binding.selectionRv.setItemViewCacheSize(20)
        binding.selectionRv.layoutManager = LinearLayoutManager(this)
        adapter = MusicPlayerAdapter(this, MainActivity.MusicListMA,selectionActivity = true)
        binding.selectionRv.adapter = adapter
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.teal_200)
        }
    }

    private fun initializeBindingListener() {
        binding.backBtnSA.setOnClickListener { finish() }
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.MusicListSearch = ArrayList()
                if (newText!=null){
                    val userInput = newText.lowercase()
                    for(song in MainActivity.MusicListMA)
                        if(song.title.lowercase().contains(userInput))
                            MainActivity.MusicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicList(searchList = MainActivity.MusicListSearch)
                }
                return true
            }
        })
    }
}