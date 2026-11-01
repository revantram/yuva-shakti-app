package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

object NotificationHelper {
    const val CHANNEL_ID = "bhati_fund_transactions_v2_channel"
    const val SIREN_CHANNEL_ID = "bhati_pending_siren_channel"
    private const val CHANNEL_NAME = "युवा शक्ति ग्रुप लेन-देन सूचनाएँ"
    private const val CHANNEL_DESC = "अंशदान, ऑनलाइन जमा और बहीखाता खर्च की लाइव सूचनाएँ"

    fun createNotificationChannel(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                val defaultSoundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = android.media.AudioAttributes.Builder()
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                    .build()

                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                    description = CHANNEL_DESC
                    enableVibration(true)
                    setShowBadge(true)
                    setSound(defaultSoundUri, audioAttributes)
                }
                notificationManager?.createNotificationChannel(channel)

                val sirenChannel = NotificationChannel(
                    SIREN_CHANNEL_ID,
                    "🚨 युवा शक्ति ग्रुप - बकाया अंशदान सायरन",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "बकाया अंशदान हेतु चेतावनी सायरन व तीव्र सूचनाएँ"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 600, 200, 600, 200, 1000, 400, 1000)
                    setShowBadge(true)
                    val sirenAttributes = android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                        .build()
                    val sirenSoundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
                        ?: defaultSoundUri
                    setSound(sirenSoundUri, sirenAttributes)
                }
                notificationManager?.createNotificationChannel(sirenChannel)
            }
        } catch (t: Throwable) {
            android.util.Log.e("NotificationHelper", "Failed to create notification channels", t)
        }
    }

    /**
     * Triggers a system notification whenever an admin or member records a transaction.
     */
    fun showTransactionNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = (System.currentTimeMillis() % 100000).toInt()
    ) {
        // Ensure channel exists
        createNotificationChannel(context)

        // Check POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // If not granted, we gracefully cannot post system notification, but in-app notification exists
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val soundUri = SoundHelper.getJaiShreeRamSoundUri(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            SoundHelper.playJaiShreeRam(context)
        } catch (e: SecurityException) {
            // Permission revoked or not granted
        } catch (e: Throwable) {
            // Catch potential bad state
        }
    }

    /**
     * Triggers a morning festival greeting notification for Hindu festivals.
     */
    fun showFestivalGreetingNotification(
        context: Context,
        festivalName: String,
        wishes: String,
        notificationId: Int = 1008
    ) {
        createNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val soundUri = SoundHelper.getJaiShreeRamSoundUri(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🎉 $festivalName की शुभकामनाएं!")
            .setContentText(wishes)
            .setStyle(NotificationCompat.BigTextStyle().bigText("🚩 युवा शक्ति ग्रुप, भाटी परिवार कुचेरा:\n$wishes"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            SoundHelper.playJaiShreeRam(context)
        } catch (e: SecurityException) {
        } catch (e: Throwable) {
        }
    }

    /**
     * Automatically triggers a local reminder notification 2 days before the Amavasya fund due date.
     */
    fun checkAndSendAmavasyaTwoDayReminder(context: Context, remainingDays: Long, targetMillis: Long) {
        if (remainingDays in 1..2) {
            val prefs = context.getSharedPreferences("amavasya_notif_prefs", Context.MODE_PRIVATE)
            val lastNotifiedCycle = prefs.getLong("last_notified_amavasya_millis", 0L)
            // Fire once per cycle when 2 days remain
            if (lastNotifiedCycle != targetMillis) {
                showTransactionNotification(
                    context = context,
                    title = "🔔 अमावस्या अंशदान सूचना (केवल $remainingDays दिन शेष)",
                    message = "सादर प्रणाम! युवा शक्ति ग्रुप की आगामी अमावस्या बैठक व फंड अंशदान में केवल $remainingDays दिन शेष हैं। कृपया अपना मासिक अंशदान समय पर जमा कराने की तैयारी रखें।",
                    notificationId = 8888
                )
                prefs.edit().putLong("last_notified_amavasya_millis", targetMillis).apply()
            }
        }
    }

    /**
     * Triggers a high-priority Siren Alarm notification for members whose monthly payment is pending or emergency broadcast.
     */
    fun triggerPendingSirenNotification(
        context: Context,
        title: String? = null,
        message: String? = null,
        memberName: String = "सदस्य",
        amount: Int = 100
    ) {
        createNotificationChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val soundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
            ?: android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)

        val actualTitle = title ?: "🚨 अत्यावश्यक सायरन तगादा: अंशदान बाकी है!"
        val actualMessage = message ?: "सादर प्रणाम $memberName जी! युवा शक्ति ग्रुप (भाटी परिवार कुचेरा) का आपका ₹$amount मासिक अमावस्या अंशदान अभी तक जमा नहीं हुआ है। कृपया तुरंत जमा कराएं।"

        val notification = NotificationCompat.Builder(context, SIREN_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(actualTitle)
            .setContentText(actualMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(actualMessage))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 600, 200, 600, 200, 1000, 400, 1000))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(9999, notification)
        } catch (e: Throwable) {
            // handle exception
        }
    }

    /**
     * Sends the daily morning inspirational thought notification to all members.
     */
    fun sendMorningThoughtNotification(context: Context, thoughtText: String? = null) {
        createNotificationChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            7777,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val actualThought = thoughtText ?: DailyThoughtHelper.getTodayThought().hindi
        val title = "🌅 सुप्रभात भाटी परिवार • आज का सुविचार"
        val message = "❝ $actualThought ❞\n\n- युवा शक्ति ग्रुप, भाटी परिवार कुचेरा"

        val soundUri = SoundHelper.getJaiShreeRamSoundUri(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText("❝ $actualThought ❞")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(7777, notification)
            SoundHelper.playJaiShreeRam(context)
        } catch (e: Throwable) {
            // handle exception
        }
    }
}
