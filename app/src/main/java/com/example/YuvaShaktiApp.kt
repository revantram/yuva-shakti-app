package com.example
 
import android.app.Application
import android.util.Log
import com.example.util.NotificationHelper
import com.google.firebase.FirebaseApp

class YuvaShaktiApp : Application() {
    companion object {
        private const val TAG = "YuvaShaktiApp"
    }

    override fun onCreate() {
        super.onCreate()

        // 1. Set global uncaught exception handler to prevent hard crashes
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "Uncaught exception on thread ${thread.name}: ${throwable.message}", throwable)
            // If main thread, attempt to let default handler manage or fail gracefully
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
