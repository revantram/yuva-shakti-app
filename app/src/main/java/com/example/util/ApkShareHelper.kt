package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object ApkShareHelper {
    /**
     * Shares the actual installed APK file via Android's share sheet (WhatsApp, Bluetooth, etc.)
     * so that the recipient can install the app directly without downloading.
     */
    fun shareActualApk(context: Context) {
        try {
            val appInfo = context.applicationInfo
            val sourceApk = File(appInfo.sourceDir)
            if (!sourceApk.exists()) {
                Toast.makeText(context, "APK फ़ाइल उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
                return
            }

            val shareDir = File(context.cacheDir, "apk_share")
            if (!shareDir.exists()) {
                shareDir.mkdirs()
            }

            val targetApk = File(shareDir, "yuva-shakti-bhati-parivar.apk")
            // Copy only if not present or size differs
            if (!targetApk.exists() || targetApk.length() != sourceApk.length()) {
                sourceApk.inputStream().use { input ->
                    targetApk.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                targetApk
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, apkUri)
                putExtra(Intent.EXTRA_SUBJECT, "युवा शक्ति ग्रुप ऐप APK")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "🚩 *युवा शक्ति ग्रुप, भाटी परिवार कुचेरा*\n\n" +
                    "सादर प्रणाम! यह भाटी परिवार की आधिकारिक एप्लिकेशन APK फ़ाइल है।\n" +
                    "इस फ़ाइल पर टैप करके ऐप सीधे अपने फोन में इंस्टॉल करें।\n\n" +
                    "📥 *डायरेक्ट डाउनलोड लिंक:*\nhttps://github.com/revantram/yuva-shakti-app/releases/latest/download/yuva-shakti.apk"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "युवा शक्ति ऐप (APK) शेयर करें"))
        } catch (e: Exception) {
            Toast.makeText(context, "APK शेयर करने में त्रुटि: ${e.localizedMessage ?: e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
