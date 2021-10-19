package com.example.musicplayer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.activities.PlayerActivity
import com.example.musicplayer.AppClass
import com.example.musicplayer.fragment.CurrentPlayingFragment
import com.example.musicplayer.R
import com.example.musicplayer.model.setSongPos
import kotlin.system.exitProcess

class NotificationBroadcastReceiver :BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
       when(intent?.action){
           AppClass.EXIT -> {
               PlayerActivity.musicService!!.stopForeground(true)
               PlayerActivity.musicService!!.mediaPlayer!!.release()
               PlayerActivity.musicService = null
               exitProcess(1)
           }
           AppClass.PLAY ->
               if (PlayerActivity.isPlaying) pauseMusic()
               else
                   playMusic()

           AppClass.PREVIOUS -> previousNextSong(increment = false,context=context!!)
           AppClass.NEXT -> previousNextSong(increment = true,context=context!!)
       }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        CurrentPlayingFragment.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
    }

    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        CurrentPlayingFragment.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
    }

    private fun previousNextSong(increment:Boolean,context:Context){
        setSongPos(increment = increment)
        PlayerActivity.musicService!!.createMusicMediaPlayer()
        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon)).centerCrop()
            .into(PlayerActivity.binding.songImgPA)
        PlayerActivity.binding.songNamePA.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title

        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon)).centerCrop()
            .into(CurrentPlayingFragment.binding.songImgNp)
        CurrentPlayingFragment.binding.songNameNp.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        playMusic()
    }
}