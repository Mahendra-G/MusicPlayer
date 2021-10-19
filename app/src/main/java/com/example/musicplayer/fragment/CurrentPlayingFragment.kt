package com.example.musicplayer.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.activities.PlayerActivity
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentCurrentPlayingBinding
import com.example.musicplayer.model.setSongPos


class CurrentPlayingFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentCurrentPlayingBinding
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_current_playing, container, false)
        binding = FragmentCurrentPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE

        initializeBindingsListeners()

        return view
    }

    private fun initializeBindingsListeners() {
        binding.playPauseBtnNP.setOnClickListener {
            if (PlayerActivity.isPlaying)
                pauseMusic()
            else
                playMusic()
        }
        binding.nextBtnNP.setOnClickListener {
            playNextSong()
        }
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class","CurrentPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService!=null){
            binding.root.visibility=View.VISIBLE
            binding.songNameNp.isSelected=true
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon)).centerCrop()
                .into(binding.songImgNp)
            binding.songNameNp.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            if (PlayerActivity.isPlaying){
                binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
            }else {
                binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
            }
        }
    }

    private fun playMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.pause_icon)
        PlayerActivity.isPlaying = true
    }

    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.nextBtnPA.setIconResource(R.drawable.play_icon)
        PlayerActivity.isPlaying = false
    }

    private fun playNextSong(){
        setSongPos(increment = true)
        PlayerActivity.musicService!!.createMusicMediaPlayer()

        Glide.with(this)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon)).centerCrop()
            .into(binding.songImgNp)

        binding.songNameNp.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        playMusic()
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class","CurrentPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
    }
}