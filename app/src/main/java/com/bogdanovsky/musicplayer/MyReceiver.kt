package com.bogdanovsky.musicplayer

import android.content.*

class MyReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent != null) {
            when (intent.action) {
                ACTION_PLAY -> {
                    val intent = Intent(ACTION_PLAY)
                    context?.sendBroadcast(intent)
                }
                ACTION_PAUSE -> {
                    val intent = Intent(ACTION_PAUSE)
                    context?.sendBroadcast(intent)
                }
                ACTION_PREVIOUS -> {
                    val intent = Intent(ACTION_PREVIOUS)
                    context?.sendBroadcast(intent)
                }
                ACTION_NEXT -> {
                    val intent = Intent(ACTION_NEXT)
                    context?.sendBroadcast(intent)
                }
            }
        }
    }
}