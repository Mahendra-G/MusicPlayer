package com.example.musicplayer.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.musicplayer.activities.PlayerActivity
import com.example.musicplayer.AppClass
import com.example.musicplayer.fragment.CurrentPlayingFragment
import com.example.musicplayer.R
import com.example.musicplayer.model.formatDuration
import com.example.musicplayer.model.getImageArt
import java.lang.Exception

class MusicService:Service(),AudioManager.OnAudioFocusChangeListener {

    private var myBinder = MyBinder()
    var mediaPlayer:MediaPlayer?=null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audiManager: AudioManager

    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext,"Music")
        return myBinder
    }

    inner class MyBinder:Binder(){
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseButton : Int){

        val intent = Intent(baseContext, PlayerActivity::class.java)
        intent.putExtra("index", PlayerActivity.songPosition)
        intent.putExtra("class","CurrentPlaying")
        val contextIntent = PendingIntent.getActivity(this,0,intent,0)


        val prevIntent = Intent(baseContext, NotificationBroadcastReceiver::class.java).setAction(
            AppClass.PREVIOUS
        )
        val prePendingIntent = PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext, NotificationBroadcastReceiver::class.java).setAction(
            AppClass.PLAY
        )
        val playPendingIntent = PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext, NotificationBroadcastReceiver::class.java).setAction(
            AppClass.NEXT
        )
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(baseContext, NotificationBroadcastReceiver::class.java).setAction(
            AppClass.EXIT
        )
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val imageArt = getImageArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if (imageArt!=null){
            BitmapFactory.decodeByteArray(imageArt,0,imageArt.size)
        }else {
            BitmapFactory.decodeResource(resources, R.drawable.music_player_icon)
        }

        val notification = NotificationCompat.Builder(baseContext, AppClass.CHANNEL_ID)
            .setContentIntent(contextIntent)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_baseline_arrow_back,"Previous",prePendingIntent)
            .addAction(playPauseButton,"Play",playPendingIntent)
            .addAction(R.drawable.next_icon,"Next",nextPendingIntent)
            .addAction(R.drawable.exit_icon,"Exit",exitPendingIntent)
            .build()

        startForeground(1,notification)
    }

    fun createMusicMediaPlayer(){
        try {
            if (PlayerActivity.musicService!!.mediaPlayer==null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()

            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)

            PlayerActivity.binding.tvSeekBarStart.text = formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text = formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.duration.toLong())

            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = PlayerActivity.musicService!!.mediaPlayer!!.duration
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id
        }catch (e: Exception){return}
    }

    fun seekBarSetUp(){
        runnable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text = formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)

    }

    override fun onAudioFocusChange(focus: Int) {
        if (focus<=0){
            // stop music
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
            CurrentPlayingFragment.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
            showNotification(R.drawable.play_icon)
            PlayerActivity.isPlaying = false
            PlayerActivity.musicService!!.mediaPlayer!!.pause()
        }else {
         //start music
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            CurrentPlayingFragment.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
            showNotification(R.drawable.pause_icon)
            PlayerActivity.isPlaying = true
            PlayerActivity.musicService!!.mediaPlayer!!.start()
        }
    }
}