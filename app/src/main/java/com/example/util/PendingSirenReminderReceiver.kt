package com.example.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

class PendingSirenReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val database = AppDatabase.getInstance(context)
        val prefs = context.getSharedPreferences("yuva_shakti_prefs", Context.MODE_PRIVATE)
        val userPhone = prefs.getString("user_phone", "") ?: ""
        val userName = prefs.getString("user_full_name", "सदस्य") ?: "सदस्य"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val members = database.memberDao().getAllMembers().firstOrNull() ?: emptyList()
                val currentMember = members.find { it.phone == userPhone }

                // Check if current user is pending, or if admin check if any pending members exist
                val isCurrentUserPending = currentMember?.status == "Pending"
                val hasPendingMembers = members.any { it.status == "Pending" }

                if (isCurrentUserPending || hasPendingMembers) {
                    val nameToAlert = if (isCurrentUserPending) userName else "भाटी परिवार सदस्य"
                    NotificationHelper.triggerPendingSirenNotification(
                        context = context,
                        memberName = nameToAlert,
                        amount = currentMember?.monthlyAmount ?: 100
                    )
                }
            } catch (e: Exception) {
                // Ignore background error
            }
        }
    }

    companion object {
        private val ALARM_HOURS = listOf(9, 12, 15, 18, 21) // 5 times a day: 9 AM, 12 PM, 3 PM, 6 PM, 9 PM

        fun scheduleFiveDailySirenAlarms(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

            ALARM_HOURS.forEachIndexed { index, hour ->
                val intent = Intent(context, PendingSirenReminderReceiver::class.java).apply {
                    action = "ACTION_PENDING_SIREN_ALARM_$hour"
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    1000 + index,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                try {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                } catch (e: Exception) {
                    // Fallback for newer Android versions
                    try {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    } catch (e2: Exception) {
                        // ignore
                    }
                }
            }
        }
    }
}
