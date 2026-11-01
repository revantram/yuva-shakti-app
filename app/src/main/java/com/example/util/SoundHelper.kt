package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.R

object SoundHelper {
    private const val TAG = "SoundHelper"
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

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
                stopSound()

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
     * Plays a loud repeating siren/alarm on STREAM_ALARM / USAGE_ALARM
     * and triggers continuous repeating vibration until stopped.
     */
    fun playSirenAlarm(context: Context) {
        val appContext = context.applicationContext
        Thread {
            try {
                stopSound()

                // 1. Continuous vibration pattern: vibrate 700ms, pause 200ms, vibrate 700ms, pause 200ms, vibrate 1000ms
                try {
                    val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val vibManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                        vibManager?.defaultVibrator
                    } else {
                        @Suppress("DEPRECATION")
                        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    }
                    vibrator = vib
                    val pattern = longArrayOf(0, 700, 200, 700, 200, 1000, 300)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vib?.vibrate(VibrationEffect.createWaveform(pattern, 0)) // 0 = repeat indefinitely
                    } else {
                        @Suppress("DEPRECATION")
                        vib?.vibrate(pattern, 0)
                    }
                } catch (t: Throwable) {
                    Log.e(TAG, "Vibrator error in playSirenAlarm", t)
                }

                // 2. Play alarm sound via MediaPlayer on USAGE_ALARM
                var player: MediaPlayer? = null
                val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

                if (alarmUri != null) {
                    try {
                        player = MediaPlayer().apply {
                            setDataSource(appContext, alarmUri)
                            setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_ALARM)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .build()
                            )
                            isLooping = true
                            prepare()
                            start()
                        }
                    } catch (t: Throwable) {
                        Log.w(TAG, "Default alarm URI failed, falling back to raw resource", t)
                        player = null
                    }
                }

                if (player == null) {
                    player = MediaPlayer.create(appContext, R.raw.jai_shree_ram)?.apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )
                        isLooping = true
                        start()
                    }
                }

                mediaPlayer = player
                Log.d(TAG, "Siren alarm and repeating vibration started")
            } catch (e: Throwable) {
                Log.e(TAG, "Error starting siren alarm", e)
            }
        }.start()
    }

    /**
     * Stops and releases any currently playing sound and vibration.
     */
    fun stopSound() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Throwable) {}
        mediaPlayer = null

        try {
            vibrator?.cancel()
        } catch (_: Throwable) {}
        vibrator = null
    }
}
