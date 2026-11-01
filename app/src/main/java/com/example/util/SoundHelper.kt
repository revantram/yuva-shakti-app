package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.R

object SoundHelper {
    private const val TAG = "SoundHelper"
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Returns the android.resource:// Uri for the Jai Shree Ram ringtone.
     */
    fun getJaiShreeRamSoundUri(context: Context): Uri {
        return Uri.parse("android.resource://${context.packageName}/${R.raw.jai_shree_ram}")
    }

    /**
     * Plays the Jai Shree Ram ringtone cleanly using MediaPlayer.
     */
    fun playJaiShreeRam(context: Context) {
        val appContext = context.applicationContext
        Thread {
            try {
                try {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                } catch (_: Throwable) {}
                mediaPlayer = null

                val player = MediaPlayer.create(appContext, R.raw.jai_shree_ram) ?: return@Thread
                player.setOnCompletionListener { mp ->
                    try {
                        mp.release()
                    } catch (_: Throwable) {}
                    if (mediaPlayer == mp) {
                        mediaPlayer = null
                    }
                }
                player.start()
                mediaPlayer = player
                Log.d(TAG, "Jai Shree Ram ringtone played successfully")
            } catch (e: Throwable) {
                Log.e(TAG, "Error playing Jai Shree Ram sound", e)
            }
        }.start()
    }

    /**
     * Stops and releases any currently playing sound.
     */
    fun stopSound() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Throwable) {}
        mediaPlayer = null
    }
}
