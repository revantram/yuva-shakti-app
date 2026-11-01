package com.example

import android.app.Application
import android.util.Log
import com.example.util.NotificationHelper
import com.google.firebase.FirebaseApp
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class YuvaShaktiApp : Application() {
    companion object {
        private const val TAG = "YuvaShaktiApp"
    }

    override fun onCreate() {
        super.onCreate()

        // 1. Global uncaught crash logger
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val stackTrace = sw.toString()
                Log.e(TAG, "FATAL CRASH on thread ${thread.name}: ${throwable.message}\n$stackTrace")

                try {
                    val crashFile = File("/sdcard/Download/yuva_shakti_crash.txt")
                    crashFile.writeText("CRASH on thread ${thread.name}:\nError: ${throwable.message}\n\nStack Trace:\n$stackTrace")
                } catch (_: Throwable) {}

                try {
                    val internalFile = File(filesDir, "crash.txt")
                    internalFile.writeText("CRASH on thread ${thread.name}:\nError: ${throwable.message}\n\nStack Trace:\n$stackTrace")
                } catch (_: Throwable) {}
            } catch (_: Throwable) {}

            defaultHandler?.uncaughtException(thread, throwable)
        }

        // 2. Safely initialize FirebaseApp
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "FirebaseApp initialized successfully")
        } catch (t: Throwable) {
            Log.e(TAG, "FirebaseApp initialization error", t)
        }

        // 3. Pre-create notification channels safely
        try {
            NotificationHelper.createNotificationChannel(this)
        } catch (t: Throwable) {
            Log.e(TAG, "Notification channel pre-creation error", t)
        }
    }
}
