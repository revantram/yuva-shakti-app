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
    const val CHANNEL_ID = "bhati_fund_transactions_channel"
    private const val CHANNEL_NAME = "युवा शक्ति ग्रुप लेन-देन सूचनाएँ"
    private const val CHANNEL_DESC = "अंशदान, ऑनलाइन जमा और बहीखाता खर्च की लाइव सूचनाएँ"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
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

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permission revoked or not granted
        } catch (e: Exception) {
            // Catch potential bad state
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
}
