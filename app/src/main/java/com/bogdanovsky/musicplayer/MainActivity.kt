package com.bogdanovsky.musicplayer

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {

    lateinit var musicService: MusicService
    lateinit var filename: TextView
    lateinit var playButton: Button
    lateinit var pauseButton: Button
    private var bound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.service
            bound = true
            musicService.filename.observe(this@MainActivity) {
                filename.text = it
            }
            musicService.isPlaying.observe(this@MainActivity) { isPlaying ->
                if (!isPlaying) {
                    playButton.visibility = View.VISIBLE
                    pauseButton.visibility = View.INVISIBLE
                } else {
                    playButton.visibility = View.INVISIBLE
                    pauseButton.visibility = View.VISIBLE
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playButton = findViewById<Button>(R.id.play)
        playButton.setOnClickListener {
            if (bound) playTrack()
        }
        pauseButton = findViewById<Button>(R.id.pause)
        pauseButton.setOnClickListener {
            if (bound) pauseTrack()
        }
        findViewById<Button>(R.id.previous).setOnClickListener {
            if (bound) previousTrack()
        }
        findViewById<Button>(R.id.next).setOnClickListener {
            if (bound) nextTrack()
        }
        filename = findViewById(R.id.filename)

        val intent = Intent(applicationContext, MusicService::class.java)
        applicationContext.startForegroundService(intent)
    }

    override fun onStart() {
        Log.i("GATT", " MainActivity onStart")
        super.onStart()
        val intent = Intent(this, MusicService::class.java)
//        if (!bound) {
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
//        }
    }

    override fun onStop() {
        if (bound) {
            unbindService(connection)
            bound = false
        }
        Log.i("GATT", " MainActivity onStop")
        super.onStop()
    }

    fun previousTrack() {
        musicService.previous()
    }

    fun playTrack() {
        musicService.play()
    }

    fun nextTrack() {
        musicService.next()
    }

    fun pauseTrack() {
        musicService.pause()
    }
}