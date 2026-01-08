package com.example.shopmanager.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import com.example.shopmanager.R

class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val tracks: List<Int> = listOf(
        R.raw.track1,
        R.raw.track2,
        R.raw.track3
    )
    private var index = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            "PLAY" -> play()
            "STOP" -> stop()
            "NEXT" -> next()
        }

        return START_STICKY
    }

    private fun play() {
        mediaPlayer = MediaPlayer.create(this, tracks[index])
        mediaPlayer.start()
    }

    private fun stop() {
        if (::mediaPlayer.isInitialized) mediaPlayer.stop()
    }

    private fun next() {
        stop()
        index = (index + 1) % tracks.size
        play()
    }

    override fun onBind(intent: Intent?) = null
}