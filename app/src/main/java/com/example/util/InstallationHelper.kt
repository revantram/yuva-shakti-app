package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import java.util.UUID

object InstallationHelper {
    private const val TAG = "InstallationHelper"
    private const val PREFS_NAME = "yuva_shakti_device_identity"
    private const val KEY_DEVICE_UUID = "device_unique_uuid"
    private const val KEY_FIRST_INSTALL_TIME = "first_install_time"

    /**
     * Retrieves or generates a permanent Unique Device UUID for this app installation.
     * Saved in persistent SharedPreferences so every phone has a distinct, immutable ID.
     */
    fun getDeviceUuid(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var uuid = prefs.getString(KEY_DEVICE_UUID, null)
        if (uuid.isNullOrBlank()) {
            uuid = "yuva_dev_" + UUID.randomUUID().toString().replace("-", "").take(16)
            prefs.edit()
                .putString(KEY_DEVICE_UUID, uuid)
                .putLong(KEY_FIRST_INSTALL_TIME, System.currentTimeMillis())
                .apply()
            Log.d(TAG, "Generated new unique device UUID: $uuid")
        }
        return uuid
    }

    /**
     * Returns device model information for transparent cloud presence diagnostics.
     */
    fun getDeviceInfo(): String {
        val manufacturer = Build.MANUFACTURER?.replaceFirstChar { it.uppercase() } ?: ""
        val model = Build.MODEL ?: ""
        return if (model.startsWith(manufacturer, ignoreCase = true)) model else "$manufacturer $model"
    }
}
