package com.bogdanovsky.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

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
                ACTION_STOP_SERVICE -> {
                    Log.i("GATT", " MusicServiceReceiver ACTION_STOP_SERVICE")
                    val intent = Intent(ACTION_STOP_SERVICE)
                    context?.dismiss()
                }
            }
        }
    }
}