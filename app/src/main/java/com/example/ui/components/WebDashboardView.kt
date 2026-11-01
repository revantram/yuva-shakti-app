package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.util.SoundHelper

class AndroidBridge(private val context: Context) {
    @JavascriptInterface
    fun playSiren() {
        SoundHelper.playSirenAlarm(context)
    }

    @JavascriptInterface
    fun stopSound() {
        SoundHelper.stopSound()
    }

    @JavascriptInterface
    fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun openUpi(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "कोई UPI ऐप (PhonePe/GPay) नहीं मिला", Toast.LENGTH_LONG).show()
        }
    }

    @JavascriptInterface
    fun openWhatsApp(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "व्हाट्सएप खोलने में समस्या हुई", Toast.LENGTH_SHORT).show()
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebDashboardView(
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var webViewRef: WebView? = remember { null }

    BackHandler {
        if (webViewRef?.canGoBack() == true) {
            webViewRef?.goBack()
        } else {
            onNavigateBack?.invoke()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.destroy()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewRef = this
                    setBackgroundColor(android.graphics.Color.parseColor("#0B0F19"))
                    
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                        allowFileAccess = true
                        allowContentAccess = true
                        mediaPlaybackRequiresUserGesture = false
                    }

                    addJavascriptInterface(AndroidBridge(ctx), "AndroidBridge")

                    webChromeClient = object : WebChromeClient() {
                        override fun onJsAlert(
                            view: WebView?,
                            url: String?,
                            message: String?,
                            result: JsResult?
                        ): Boolean {
                            android.app.AlertDialog.Builder(ctx)
                                .setTitle("युवा शक्ति ग्रुप")
                                .setMessage(message)
                                .setPositiveButton("ठीक है") { _, _ -> result?.confirm() }
                                .setCancelable(false)
                                .show()
                            return true
                        }

                        override fun onJsConfirm(
                            view: WebView?,
                            url: String?,
                            message: String?,
                            result: JsResult?
                        ): Boolean {
                            android.app.AlertDialog.Builder(ctx)
                                .setTitle("युवा शक्ति ग्रुप")
                                .setMessage(message)
                                .setPositiveButton("हाँ") { _, _ -> result?.confirm() }
                                .setNegativeButton("नहीं") { _, _ -> result?.cancel() }
                                .setCancelable(false)
                                .show()
                            return true
                        }

                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            android.util.Log.d("WebDashboard", "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}")
                            return true
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url?.toString() ?: return false
                            return handleCustomUrl(ctx, url)
                        }

                        @Deprecated("Deprecated in Java")
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            url: String?
                        ): Boolean {
                            if (url == null) return false
                            return handleCustomUrl(ctx, url)
                        }
                    }

                    // Load index.html directly from local assets with secure Firebase base URL
                    try {
                        val htmlContent = ctx.assets.open("index.html").bufferedReader().use { it.readText() }
                        loadDataWithBaseURL(
                            "https://yuva-shakti-group.firebaseapp.com/",
                            htmlContent,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("WebDashboard", "Failed to load index.html from assets", e)
                        loadUrl("file:///android_asset/index.html")
                    }
                }
            }
        )
    }
}

private fun handleCustomUrl(context: Context, url: String): Boolean {
    return try {
        if (url.startsWith("upi://")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } else if (url.startsWith("https://chat.whatsapp.com") || url.startsWith("whatsapp://")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } else if (url.endsWith(".apk") || url.contains("/releases/download/")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } else {
            false
        }
    } catch (e: Exception) {
        android.util.Log.e("WebDashboard", "Error launching external intent for $url", e)
        false
    }
}
