package com.example

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.webkit.ConsoleMessage
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris != null && uris.isNotEmpty()) {
            filePathCallback?.onReceiveValue(uris.toTypedArray())
        } else {
            filePathCallback?.onReceiveValue(null)
        }
        filePathCallback = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hardware acceleration for ultra-smooth 60/120fps rendering
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        // Status bar & navigation bar theme colors matching iOS Liquid Glass theme
        try {
            window.statusBarColor = android.graphics.Color.parseColor("#0f172a")
            window.navigationBarColor = android.graphics.Color.parseColor("#0f172a")
        } catch (_: Throwable) {}

        webView = WebView(this).apply {
            setBackgroundColor(android.graphics.Color.parseColor("#0f172a"))

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                allowFileAccess = true
                allowContentAccess = true
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
                ): Boolean {
                    AlertDialog.Builder(this@MainActivity)
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
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("युवा शक्ति ग्रुप")
                        .setMessage(message)
                        .setPositiveButton("हाँ") { _, _ -> result?.confirm() }
                        .setNegativeButton("नहीं") { _, _ -> result?.cancel() }
                        .setCancelable(false)
                        .show()
                    return true
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    this@MainActivity.filePathCallback?.onReceiveValue(null)
                    this@MainActivity.filePathCallback = filePathCallback
                    fileChooserLauncher.launch("image/*")
                    return true
                }

                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    android.util.Log.d("YuvaShakti", "${consoleMessage?.message()} -- line ${consoleMessage?.lineNumber()}")
                    return true
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return false
                    return handleExternalUrl(url)
                }

                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    url: String?
                ): Boolean {
                    if (url == null) return false
                    return handleExternalUrl(url)
                }
            }

            // Load local asset web application
            loadUrl("file:///android_asset/index.html")
        }

        setContentView(webView)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun handleExternalUrl(url: String): Boolean {
        return try {
            if (url.startsWith("upi://") ||
                url.startsWith("whatsapp://") ||
                url.startsWith("https://api.whatsapp.com") ||
                url.startsWith("https://chat.whatsapp.com") ||
                url.startsWith("tel:") ||
                url.startsWith("mailto:")
            ) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                true
            } else if (url.startsWith("http://") || url.startsWith("https://")) {
                // Internal API/CDN requests keep inside WebView; other external pages open in browser
                if (url.contains("firebaseapp.com") ||
                    url.contains("googleapis.com") ||
                    url.contains("qrserver.com") ||
                    url.contains("unsplash.com") ||
                    url.contains("cloudflare.com") ||
                    url.contains("gstatic.com") ||
                    url.contains("firebasestorage.app") ||
                    url.contains("firebasedatabase.app") ||
                    url.contains("cdnjs.cloudflare.com")
                ) {
                    false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Toast.makeText(this, "लिंक खोलने में समस्या: ${e.message}", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
