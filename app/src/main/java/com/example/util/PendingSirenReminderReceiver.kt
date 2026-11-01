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
        val action = intent?.action ?: ""

        // 1. Morning Daily Thought Notification (सुबह का दैनिक सुविचार)
        if (action == "ACTION_MORNING_THOUGHT_ALARM") {
            NotificationHelper.sendMorningThoughtNotification(context)
            return
        }

        // 2. Pending Members Siren Alarm (बकाया अंशदान सायरन)
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
        private val SIREN_HOURS = listOf(9, 12, 15, 18, 21) // 5 times a day: 9 AM, 12 PM, 3 PM, 6 PM, 9 PM
        private const val MORNING_THOUGHT_HOUR = 7 // 7:00 AM every morning

        fun scheduleAllDailyAlarms(context: Context) {
            try {
                scheduleFiveDailySirenAlarms(context)
                scheduleDailyMorningThoughtAlarm(context)
                checkAndSendTodayMorningThought(context)
            } catch (t: Throwable) {
                android.util.Log.e("PendingSirenReminder", "Error scheduling daily alarms", t)
            }
        }

        fun scheduleDailyMorningThoughtAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, PendingSirenReminderReceiver::class.java).apply {
                action = "ACTION_MORNING_THOUGHT_ALARM"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                7000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, MORNING_THOUGHT_HOUR)
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

        fun checkAndSendTodayMorningThought(context: Context) {
            try {
                val prefs = context.getSharedPreferences("yuva_shakti_thought_prefs", Context.MODE_PRIVATE)
                val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val todayKey = "$currentYear-$currentDay"
                val lastNotified = prefs.getString("last_thought_day", "")

                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                // If morning (between 6 AM and 12 PM) and not notified yet today, send notification
                if (currentHour in 6..12 && lastNotified != todayKey) {
                    NotificationHelper.sendMorningThoughtNotification(context)
                    prefs.edit().putString("last_thought_day", todayKey).apply()
                }
            } catch (t: Throwable) {
                android.util.Log.e("PendingSirenReminder", "Error sending today morning thought", t)
            }
        }

        fun scheduleFiveDailySirenAlarms(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

            SIREN_HOURS.forEachIndexed { index, hour ->
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
