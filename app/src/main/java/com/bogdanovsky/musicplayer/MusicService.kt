package com.bogdanovsky.musicplayer

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData

val songList = listOf(
    "Alex Attaryan - Wind.mp3",
    "IKIGUYS_Rocky_Path.mp3",
    "Sherbet Strike - Light Crusader.mp3",
    "Sinitana - Keep the Light.mp3",
    "Stalker 591 - Rebeca.mp3"
    )
const val ACTION_PLAY: String = "com.bogdanovsky.musicplayer.action.PLAY"
const val ACTION_PAUSE: String = "com.bogdanovsky.musicplayer.action.PAUSE"
const val ACTION_NEXT: String = "com.bogdanovsky.musicplayer.action.NEXT"
const val ACTION_PREVIOUS: String = "com.bogdanovsky.musicplayer.action.PREVIOUS"
const val NOTIFICATION_CHANNEL_ID = "com.bogdanovsky.musicplayer.mychannel"
const val MUSIC_SERVICE_ID = 123

class MusicService : Service(), MediaPlayer.OnPreparedListener {

    private val binder = LocalBinder()
    private var currentSongNumber = 0
    val filename = MutableLiveData<String>(songList[currentSongNumber])
    private var mediaPlayer = MediaPlayer()
    val isPlaying = MutableLiveData<Boolean>(false)
    private val musicServiceReceiver = MusicServiceReceiver()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val filter = IntentFilter()
        filter.addAction(ACTION_PLAY)
        filter.addAction(ACTION_PAUSE)
        filter.addAction(ACTION_PREVIOUS)
        filter.addAction(ACTION_NEXT)
        registerReceiver(musicServiceReceiver, filter)

        val activityIntent = Intent(this, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            this,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE)

        val playIntent = Intent(this, MyReceiver::class.java)
        playIntent.action = ACTION_PLAY
        val playPendingIntent = PendingIntent.getBroadcast(
            this,
            2,
            playIntent,
            PendingIntent.FLAG_IMMUTABLE)

        val pauseIntent = Intent(this, MyReceiver::class.java)
        pauseIntent.action = ACTION_PAUSE
        val pausePendingIntent = PendingIntent.getBroadcast(
            this,
            3,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE)

        val previousIntent = Intent(this, MyReceiver::class.java)
        previousIntent.action = ACTION_PREVIOUS
        val previousPendingIntent = PendingIntent.getBroadcast(
            this,
            4,
            previousIntent,
            PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(this, MyReceiver::class.java)
        nextIntent.action = ACTION_NEXT
        val nextPendingIntent = PendingIntent.getBroadcast(
            this,
            5,
            nextIntent,
            PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setContentIntent(activityPendingIntent)
            .addAction(R.drawable.baseline_skip_previous_24, "Previous", previousPendingIntent)
            .addAction(R.drawable.baseline_pause_24, "Pause", pausePendingIntent)
            .addAction(R.drawable.baseline_play_arrow_24, "Play", playPendingIntent)
            .addAction(R.drawable.baseline_skip_next_24, "Next", nextPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .build()
        startForeground(MUSIC_SERVICE_ID, notification)
        setUpMediaPlayer()
        return super.onStartCommand(intent, flags, startId)
    }

    inner class LocalBinder : Binder() {
        val service = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        if (isPlaying.value == true) mediaPlayer?.start()
    }

    override fun onDestroy() {
        unregisterReceiver(musicServiceReceiver)
        super.onDestroy()
    }

    private fun setUpMediaPlayer() {
        mediaPlayer.reset()
        val afd = assets.openFd(songList[currentSongNumber])

        mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length) // initialize it here
        mediaPlayer?.apply {
            setOnPreparedListener(this@MusicService)
            prepareAsync() // prepare async to not block main thread
        }
    }

    fun play() {
        mediaPlayer?.start()
        isPlaying.value = true
    }

    fun pause() {
        mediaPlayer?.pause()
        isPlaying.value = false
    }

    fun next() {
        if (currentSongNumber != songList.size - 1) {
            filename.value = songList[++currentSongNumber]
            setUpMediaPlayer()
        }
    }

    fun previous() {
        if (currentSongNumber != 0) {
            filename.value = songList[--currentSongNumber]
            setUpMediaPlayer()
        }
    }
}

class MusicServiceReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent != null) {
            context as MusicService
            when (intent.action) {
                ACTION_PLAY -> {
                    val intent = Intent(ACTION_PLAY)
                    context?.play()
                }
                ACTION_PAUSE -> {
                    val intent = Intent(ACTION_PAUSE)
                    context?.pause()
                }
                ACTION_PREVIOUS -> {
                    val intent = Intent(ACTION_PREVIOUS)
                    context?.previous()
                }
                ACTION_NEXT -> {
                    val intent = Intent(ACTION_NEXT)
                    context?.next()
                }
            }
        }
    }
}