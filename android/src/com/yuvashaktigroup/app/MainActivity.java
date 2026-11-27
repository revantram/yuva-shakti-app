package com.yuvashaktigroup.app;

import android.app.Activity;
import android.accounts.AccountManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import java.util.List;
import java.util.ArrayList;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.util.Base64;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private WebView mWebView;
    private long mLastBackPressTime = 0;
    private ValueCallback<Uri[]> mFilePathCallback;
    private static final int FILECHOOSER_RESULTCODE = 1001;
    private static final int GOOGLE_SIGNIN_REQUEST_CODE = 1002;

    public class WebAppInterface {
        @JavascriptInterface
        public String getAppVersionName() {
            try {
                return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (Exception e) {
                return "5.5.38";
            }
        }

        @JavascriptInterface
        public int getAppVersionCode() {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    return (int) getPackageManager().getPackageInfo(getPackageName(), 0).getLongVersionCode();
                } else {
                    return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                }
            } catch (Exception e) {
                return 588;
            }
        }

        @JavascriptInterface
        public void promptGoogleAccountPicker() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent intent = AccountManager.newChooseAccountIntent(
                            null, null, new String[]{"com.google"}, null, null, null, null
                        );
                        startActivityForResult(intent, GOOGLE_SIGNIN_REQUEST_CODE);
                    } catch (Exception e) {
                        sendGoogleSignInResult(false, null, "Google Account Picker error: " + e.getMessage());
                    }
                }
            });
        }
        @JavascriptInterface
        public void downloadAndInstall(String apkUrl, String versionName) {
            downloadAndInstallApk(apkUrl);
        }

        @JavascriptInterface
        public void shareAppApk() {
            shareApplicationApk();
        }

        @JavascriptInterface
        public void shareImage(String base64Data, String caption) {
            shareGeneratedImage(base64Data, caption);
        }

        @JavascriptInterface
        public void downloadImage(String base64Data, String filename) {
            saveImageDirectlyToGallery(base64Data, filename);
        }

        @JavascriptInterface
        public void printLedgerPdf() {
            printLedgerReport();
        }

        @JavascriptInterface
        public void saveLedgerPdf(String summaryJson, String transactionsJson) {
            generateAndSaveLedgerPdf(summaryJson, transactionsJson);
        }

        @JavascriptInterface
        public void openWhatsApp(String url) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "व्हाट्सएप उपलब्ध नहीं है", Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void shareText(String text, String title) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(intent, title != null ? title : "शेयर करें"));
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "शेयर करने में त्रुटि", Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void postNotification(String title, String message) {
            showSystemNotification(title, message);
        }

        @JavascriptInterface
        public void requestNotificationPermission() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkAndRequestNotificationPermission();
                }
            });
        }

        @JavascriptInterface
        public String fetchHttpUrl(String targetUrl) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(targetUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile)");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                int code = conn.getResponseCode();
                if (code >= 200 && code < 400) {
                    InputStream in = conn.getInputStream();
                    java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    in.close();
                    return new String(out.toByteArray(), "UTF-8");
                }
            } catch (Exception e) {
                // Silently return null on network failure
            } finally {
                if (conn != null) conn.disconnect();
            }
            return null;
        }

        @JavascriptInterface
        public boolean hasNotificationPermission() {
            if (Build.VERSION.SDK_INT >= 33) {
                return checkSelfPermission("android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED;
            }
            return true;
        }
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission("android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 200);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndRequestNotificationPermission();

        if (Build.VERSION.SDK_INT >= 33) {
            try {
                java.lang.reflect.Method getDispatcher = getClass().getMethod("getOnBackInvokedDispatcher");
                Object dispatcher = getDispatcher.invoke(this);
                if (dispatcher != null) {
                    Class<?> callbackClass = Class.forName("android.window.OnBackInvokedCallback");
                    Object callback = java.lang.reflect.Proxy.newProxyInstance(
                        getClassLoader(),
                        new Class<?>[]{callbackClass},
                        new java.lang.reflect.InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
                                if ("onBackInvoked".equals(method.getName())) {
                                    handleBackNavigation();
                                }
                                return null;
                            }
                        }
                    );
                    java.lang.reflect.Method register = dispatcher.getClass().getMethod("registerOnBackInvokedCallback", int.class, callbackClass);
                    register.invoke(dispatcher, 0, callback);
                }
            } catch (Throwable ignored) {}
        }

        mWebView = new WebView(this);
        mWebView.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null);
        setContentView(mWebView);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setEnableSmoothTransition(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);

        // Google Sign-In & OAuth compatibility in WebView
        String defaultUa = settings.getUserAgentString();
        String cleanUa = defaultUa.replace("; wv", "").replaceAll("Version/\\d+\\.\\d+\\s*", "");
        settings.setUserAgentString(cleanUa);
        try {
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        } catch (Exception ignored) {}

        mWebView.addJavascriptInterface(new WebAppInterface(), "AndroidApp");

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                downloadAndInstallApk(url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result != null ? result.getExtra() : null;
                if (data != null && (data.endsWith(".apk") || data.contains("/releases/download/"))) {
                    downloadAndInstallApk(data);
                    return false;
                }

                // Popup dialog for OAuth & Phone.email Sign-In
                try {
                    final android.app.Dialog popupDialog = new android.app.Dialog(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                    WebView popupWebView = new WebView(MainActivity.this);
                    WebSettings pSettings = popupWebView.getSettings();
                    pSettings.setJavaScriptEnabled(true);
                    pSettings.setDomStorageEnabled(true);
                    pSettings.setDatabaseEnabled(true);
                    pSettings.setSupportMultipleWindows(true);
                    pSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                    String defaultUa = pSettings.getUserAgentString();
                    String cleanUa = defaultUa.replace("; wv", "").replaceAll("Version/\\d+\\.\\d+\\s*", "");
                    pSettings.setUserAgentString(cleanUa);
                    try {
                        android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(popupWebView, true);
                    } catch (Exception ignored) {}

                    popupWebView.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public void onCloseWindow(WebView window) {
                            if (popupDialog.isShowing()) {
                                popupDialog.dismiss();
                            }
                        }
                    });

                    popupWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            return false;
                        }
                    });

                    popupDialog.setContentView(popupWebView);
                    popupDialog.show();

                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(popupWebView);
                    resultMsg.sendToTarget();
                    return true;
                } catch (Exception e) {
                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "प्रोफाइल फोटो चुनें");

                try {
                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                    return true;
                } catch (Exception e) {
                    mFilePathCallback = null;
                    return false;
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                // Handle PhonePe & UPI payments
                if (url.startsWith("upi://") || url.startsWith("phonepe://")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "PhonePe / UPI ऐप नहीं मिला", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                // Handle intent:// schemes
                if (url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            if (getPackageManager().resolveActivity(intent, 0) != null) {
                                startActivity(intent);
                                return true;
                            }
                            String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                            if (fallbackUrl != null) {
                                view.loadUrl(fallbackUrl);
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "ऐप खोलने में असमर्थ", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                // Handle APK downloads directly
                if (url.endsWith(".apk") || url.contains("/releases/download/")) {
                    downloadAndInstallApk(url);
                    return true;
                }

                // Handle telephone calls
                if (url.startsWith("tel:")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "कॉल करने में असमर्थ", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                // Handle WhatsApp links & group invites
                if (url.contains("chat.whatsapp.com") || url.contains("wa.me") || url.contains("api.whatsapp.com") || url.contains("whatsapp://")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "व्हाट्सएप उपलब्ध नहीं है", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                // Handle Instagram links
                if (url.contains("instagram.com")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "इंस्टाग्राम खोलने में असमर्थ", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                // Normal internal web navigation
                if (url.startsWith("file:") || url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        });

        // Load the local HTML file from assets
        mWebView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GOOGLE_SIGNIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null && !accountName.trim().isEmpty()) {
                    sendGoogleSignInResult(true, accountName.trim(), null);
                } else {
                    sendGoogleSignInResult(false, null, "कोई Google खाता नहीं चुना गया");
                }
            } else {
                sendGoogleSignInResult(false, null, "Google लॉगिन रद्द किया गया");
            }
            return;
        }

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                mFilePathCallback = null;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void sendGoogleSignInResult(final boolean success, final String email, final String error) {
        if (mWebView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject res = new JSONObject();
                        res.put("success", success);
                        if (email != null) res.put("email", email);
                        if (error != null) res.put("error", error);
                        String script = "if (typeof window.onNativeGoogleSignInResult === 'function') { window.onNativeGoogleSignInResult(" + res.toString() + "); }";
                        mWebView.evaluateJavascript(script, null);
                    } catch (Exception ignored) {}
                }
            });
        }
    }

    public void downloadAndInstallApk(final String url) {
        if (url == null || url.trim().isEmpty()) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast.makeText(MainActivity.this, "📲 ऐप अपडेट डाउनलोड शुरू हो रहा है...", Toast.LENGTH_SHORT).show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!getPackageManager().canRequestPackageInstalls()) {
                            try {
                                Intent reqIntent = new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                                startActivity(reqIntent);
                                Toast.makeText(MainActivity.this, "कृपया ऐप अपडेट इंस्टॉल की अनुमति दें", Toast.LENGTH_LONG).show();
                            } catch (Exception ignored) {}
                        }
                    }

                    // Start background download thread with real-time in-app progress updates
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            downloadApkWithProgress(url);
                        }
                    }).start();

                } catch (Exception e) {
                    fallbackBrowserDownload(url);
                }
            }
        });
    }

    private void downloadApkWithProgress(final String downloadUrl) {
        InputStream in = null;
        OutputStream out = null;
        HttpURLConnection conn = null;
        File cacheApk = null;

        try {
            // Initial progress 0%
            postDownloadProgress(0, -1, 0);

            // Follow HTTP redirects (GitHub releases redirect to AWS S3)
            String targetUrl = downloadUrl;
            int redirectCount = 0;
            while (redirectCount < 6) {
                URL u = new URL(targetUrl);
                conn = (HttpURLConnection) u.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(30000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 YuvaShaktiApp/Android");
                conn.connect();

                int status = conn.getResponseCode();
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == 307 || status == 308) {
                    String newUrl = conn.getHeaderField("Location");
                    if (newUrl != null && !newUrl.isEmpty()) {
                        conn.disconnect();
                        targetUrl = newUrl;
                        redirectCount++;
                        continue;
                    }
                }
                break;
            }

            if (conn == null) throw new Exception("सर्वर से कनेक्शन स्थापित नहीं हो सका");

            final long contentLength = Build.VERSION.SDK_INT >= 24 ? conn.getContentLengthLong() : conn.getContentLength();
            in = conn.getInputStream();

            String apkFileName = "YuvaShaktiGroup.apk";
            try {
                String path = new URL(targetUrl).getPath();
                int lastSlash = path.lastIndexOf('/');
                if (lastSlash >= 0 && lastSlash < path.length() - 1) {
                    String sub = path.substring(lastSlash + 1);
                    if (sub.toLowerCase().endsWith(".apk")) {
                        apkFileName = sub;
                    }
                }
            } catch (Exception ignored) {}

            cacheApk = new File(getCacheDir(), apkFileName);
            if (cacheApk.exists()) cacheApk.delete();

            out = new FileOutputStream(cacheApk);
            byte[] buffer = new byte[16384];
            int bytesRead;
            long totalRead = 0;
            long lastProgressUpdate = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                long now = System.currentTimeMillis();
                if (now - lastProgressUpdate > 120) {
                    lastProgressUpdate = now;
                    final int percent = (contentLength > 0) ? (int)((totalRead * 100) / contentLength) : -1;
                    postDownloadProgress(totalRead, contentLength, percent);
                }
            }
            out.flush();

            // 100% complete
            postDownloadProgress(totalRead, totalRead, 100);

            // Also copy to Downloads folder for safety and offline storage
            try {
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (downloadDir != null && (downloadDir.exists() || downloadDir.mkdirs())) {
                    File pubApk = new File(downloadDir, apkFileName);
                    copyFile(cacheApk, pubApk);
                }
            } catch (Exception ignored) {}

            final File finalApk = cacheApk;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWebView != null) {
                        mWebView.evaluateJavascript("if(window.onAppDownloadComplete) window.onAppDownloadComplete();", null);
                    }
                    promptApkInstall(finalApk);
                }
            });

        } catch (Exception ex) {
            final String errMsg = ex.getMessage();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWebView != null) {
                        mWebView.evaluateJavascript("if(window.onAppDownloadError) window.onAppDownloadError('" + (errMsg != null ? errMsg.replace("'", "\\'") : "डाउनलोड त्रुटि") + "');", null);
                    }
                    fallbackBrowserDownload(downloadUrl);
                }
            });
        } finally {
            try { if (in != null) in.close(); } catch (Exception ignored) {}
            try { if (out != null) out.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.disconnect(); } catch (Exception ignored) {}
        }
    }

    private void postDownloadProgress(final long bytesDownloaded, final long totalBytes, final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWebView != null) {
                    mWebView.evaluateJavascript("if(window.onAppDownloadProgress) window.onAppDownloadProgress(" + bytesDownloaded + "," + totalBytes + "," + percent + ");", null);
                }
            }
        });
    }

    private void promptApkInstall(File apkFile) {
        try {
            Uri apkUri = Uri.parse("content://" + AppFileProvider.AUTHORITY + "/" + apkFile.getName());
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(installIntent);
        } catch (Exception ex) {
            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(installIntent);
            } catch (Exception ex2) {
                Toast.makeText(MainActivity.this, "डाउनलोड पूर्ण हुआ! डाउनलोड्स फोल्डर से APK इंस्टॉल करें", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fallbackBrowserDownload(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "डाउनलोड शुरू नहीं हो सका: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void shareApplicationApk() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "📦 एप्लिकेशन APK तैयार हो रही है...", Toast.LENGTH_SHORT).show();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File source = new File(getPackageCodePath());
                    if (!source.exists() || source.length() == 0) {
                        source = new File(getApplicationInfo().sourceDir);
                    }

                    File apkDir = new File(getCacheDir(), "shared_apk");
                    if (!apkDir.exists()) {
                        apkDir.mkdirs();
                    }
                    File shareApk = new File(apkDir, "YuvaShaktiGroup.apk");

                    if (!shareApk.exists() || shareApk.length() != source.length()) {
                        copyFile(source, shareApk);
                    }

                    try {
                        File extCache = getExternalCacheDir();
                        if (extCache != null) {
                            File extApkDir = new File(extCache, "shared_apk");
                            if (!extApkDir.exists()) extApkDir.mkdirs();
                            File extShareApk = new File(extApkDir, "YuvaShaktiGroup.apk");
                            if (!extShareApk.exists() || extShareApk.length() != source.length()) {
                                copyFile(source, extShareApk);
                            }
                        }
                    } catch (Exception ignored) {}

                    try {
                        File dlApk = new File("/sdcard/Download/YuvaShaktiGroup.apk");
                        if (!dlApk.exists() || dlApk.length() != source.length()) {
                            copyFile(source, dlApk);
                        }
                    } catch (Exception ignored) {}

                    final Uri apkUri = Uri.parse("content://" + AppFileProvider.AUTHORITY + "/shared_apk/YuvaShaktiGroup.apk");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("application/vnd.android.package-archive");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, apkUri);

                                String shareText = "🚩 *युवा शक्ति ग्रुप (भाटी परिवार • कुचेरा)* 🚩\n\n"
                                    + "आधिकारिक परिवार प्रबंधन एवं बही-खाता एप्लिकेशन (APK फाइल)।\n\n"
                                    + "📲 *सीधा APK डाउनलोड लिंक:*\n"
                                    + "https://github.com/revantram/yuva-shakti-app/releases/latest/download/YuvaShaktiGroup.apk\n\n"
                                    + "💬 *व्हाट्सएप ग्रुप लिंक:*\n"
                                    + "https://chat.whatsapp.com/JOVKSohSoBHEokrCXOSkTh";

                                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "युवा शक्ति ग्रुप ऐप APK");

                                shareIntent.setClipData(ClipData.newRawUri("YuvaShaktiGroup.apk", apkUri));
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
                                for (ResolveInfo resolveInfo : resInfoList) {
                                    String packageName = resolveInfo.activityInfo.packageName;
                                    grantUriPermission(packageName, apkUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                }

                                boolean whatsappFound = false;
                                for (String pkg : new String[]{"com.whatsapp", "com.whatsapp.w4b"}) {
                                    try {
                                        getPackageManager().getPackageInfo(pkg, 0);
                                        shareIntent.setPackage(pkg);
                                        grantUriPermission(pkg, apkUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        whatsappFound = true;
                                        break;
                                    } catch (Exception ignored) {}
                                }

                                if (!whatsappFound) {
                                    shareIntent.setPackage(null);
                                }

                                try {
                                    startActivity(shareIntent);
                                } catch (Exception ex) {
                                    shareIntent.setPackage(null);
                                    Intent chooser = Intent.createChooser(shareIntent, "युवा शक्ति ऐप APK शेयर करें");
                                    chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(chooser);
                                }
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "शेयर त्रुटि: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "APK तैयार करने में त्रुटि: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void shareGeneratedImage(final String base64Data, final String caption) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String cleanBase64 = base64Data;
                    if (cleanBase64.contains(",")) {
                        cleanBase64 = cleanBase64.substring(cleanBase64.indexOf(",") + 1);
                    }
                    byte[] imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT);

                    // 1. Save to external Pictures directory for user's photo gallery
                    try {
                        File picturesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YuvaShakti");
                        if (!picturesDir.exists()) {
                            picturesDir.mkdirs();
                        }
                        File imageFile = new File(picturesDir, "suvichar_" + System.currentTimeMillis() + ".jpg");
                        FileOutputStream fos = new FileOutputStream(imageFile);
                        fos.write(imageBytes);
                        fos.flush();
                        fos.close();
                        MediaScannerConnection.scanFile(MainActivity.this, new String[]{imageFile.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
                    } catch (Exception ignored) {}

                    // 2. Save to internal cache for secure content:// URI sharing
                    File cacheImgDir = new File(getCacheDir(), "shared_images");
                    if (!cacheImgDir.exists()) {
                        cacheImgDir.mkdirs();
                    }
                    File cacheImage = new File(cacheImgDir, "suvichar_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream fos2 = new FileOutputStream(cacheImage);
                    fos2.write(imageBytes);
                    fos2.flush();
                    fos2.close();

                    Uri imageUri = Uri.parse("content://" + AppFileProvider.AUTHORITY + "/shared_images/" + cacheImage.getName());

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/jpeg");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    if (caption != null && !caption.isEmpty()) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT, caption);
                    }
                    shareIntent.setClipData(ClipData.newRawUri("suvichar.jpg", imageUri));
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    boolean whatsappFound = false;
                    for (String pkg : new String[]{"com.whatsapp", "com.whatsapp.w4b"}) {
                        try {
                            getPackageManager().getPackageInfo(pkg, 0);
                            shareIntent.setPackage(pkg);
                            grantUriPermission(pkg, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            whatsappFound = true;
                            break;
                        } catch (Exception ignored) {}
                    }
                    if (!whatsappFound) {
                        shareIntent.setPackage(null);
                    }

                    try {
                        startActivity(shareIntent);
                    } catch (Exception ex) {
                        shareIntent.setPackage(null);
                        Intent chooser = Intent.createChooser(shareIntent, "सुविचार फोटो शेयर करें");
                        chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(chooser);
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "फोटो शेयर करने में त्रुटि: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveImageDirectlyToGallery(final String base64Data, final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String cleanBase64 = base64Data;
                    if (cleanBase64.contains(",")) {
                        cleanBase64 = cleanBase64.substring(cleanBase64.indexOf(",") + 1);
                    }
                    byte[] imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT);
                    File picturesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YuvaShakti");
                    if (!picturesDir.exists()) {
                        picturesDir.mkdirs();
                    }
                    String fname = (filename != null && !filename.trim().isEmpty()) ? filename : ("photo_" + System.currentTimeMillis() + ".jpg");
                    if (!fname.toLowerCase().endsWith(".jpg") && !fname.toLowerCase().endsWith(".jpeg") && !fname.toLowerCase().endsWith(".png")) {
                        fname = fname + ".jpg";
                    }
                    File imageFile = new File(picturesDir, fname);
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    fos.write(imageBytes);
                    fos.flush();
                    fos.close();
                    MediaScannerConnection.scanFile(MainActivity.this, new String[]{imageFile.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "✅ फोटो आपकी गैलरी (Pictures/YuvaShakti) में सेव हो गई!", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "फोटो सेव करने में त्रुटि: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void printLedgerReport() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                    if (printManager != null) {
                        String jobName = "युवा शक्ति ग्रुप - बही-खाता रिपोर्ट";
                        PrintDocumentAdapter printAdapter = mWebView.createPrintDocumentAdapter(jobName);
                        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "प्रिंट / पीडीएफ तैयार करने में असमर्थ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void generateAndSaveLedgerPdf(final String summaryJson, final String transactionsJson) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PdfDocument document = null;
                try {
                    JSONObject summary = new JSONObject(summaryJson != null ? summaryJson : "{}");
                    JSONArray transactions = new JSONArray(transactionsJson != null ? transactionsJson : "[]");

                    boolean isEnglish = "en".equalsIgnoreCase(summary.optString("lang", "hi"));
                    String reportType = summary.optString("reportType", "");
                    String filePrefix = summary.optString("filePrefix", "Yuva_Shakti_BahiKhata_");
                    boolean isExpenseReport = "expense".equalsIgnoreCase(reportType) || filePrefix.contains("Kharch");

                    long totalIn = summary.optLong("totalIn", 0);
                    long totalOut = summary.optLong("totalOut", 0);
                    long net = summary.optLong("net", totalIn - totalOut);
                    int count = summary.optInt("count", transactions.length());
                    String dateStr = summary.optString("date", "");
                    String breakdownText = summary.optString("breakdownText", "");

                    String defaultInLabel = isEnglish ? "TOTAL DEPOSITS" : (isExpenseReport ? "कुल प्रारंभिक व जमा कोष" : "कुल जमा (अंशदान)");
                    String defaultOutLabel = isEnglish ? "TOTAL EXPENSES" : "कुल खर्च (व्यय)";
                    String defaultNetLabel = isEnglish ? "NET BALANCE" : (isExpenseReport ? "उपलब्ध कुल कोष" : "उपलब्ध शेष राशि");

                    String inLabel = summary.optString("totalInLabel", defaultInLabel);
                    String outLabel = summary.optString("totalOutLabel", defaultOutLabel);
                    String netLabel = summary.optString("netLabel", defaultNetLabel);

                    document = new PdfDocument();
                    int pageWidth = 595;
                    int pageHeight = 842;
                    int pageNumber = 1;

                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();

                    Paint paint = new Paint();
                    paint.setAntiAlias(true);

                    // 1. Draw Header Banner
                    paint.setColor(Color.parseColor("#1e3a8a"));
                    canvas.drawRoundRect(new RectF(36, 32, 559, 104), 8, 8, paint);

                    paint.setColor(Color.WHITE);
                    paint.setTextSize(16);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    String defaultTitle = isEnglish ? "Yuva Shakti Group (Bhati Parivar Kuchera)" : "युवा शक्ति ग्रुप (भाटी परिवार कुचेरा)";
                    String title = summary.optString("title", defaultTitle);
                    canvas.drawText(title, 50, 60, paint);

                    paint.setColor(Color.parseColor("#fbbf24"));
                    paint.setTextSize(11);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    String defaultSub = isEnglish ? "Official Ledger & Financial Statement" : "सामग्री खर्च एवं व्यय स्प्रेडशीट रिपोर्ट";
                    String sub = summary.optString("subTitle", defaultSub);
                    canvas.drawText(sub, 50, 78, paint);

                    paint.setColor(Color.parseColor("#e0e7ff"));
                    paint.setTextSize(9);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    String dateMeta = (isEnglish ? "Report Date: " : "रिपोर्ट दिनांक: ") + dateStr + "  |  " + (isEnglish ? "Total Items: " : "कुल सामग्री मद: ") + count;
                    canvas.drawText(dateMeta, 50, 94, paint);

                    // 2. Summary Boxes (3 horizontal cards)
                    float boxY1 = 114;
                    float boxY2 = 180;
                    float boxW = 166;

                    // Box 1: Total In / Total Available Fund
                    paint.setColor(Color.parseColor("#ecfdf5"));
                    canvas.drawRoundRect(new RectF(36, boxY1, 36 + boxW, boxY2), 6, 6, paint);
                    paint.setColor(Color.parseColor("#a7f3d0"));
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRoundRect(new RectF(36, boxY1, 36 + boxW, boxY2), 6, 6, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.parseColor("#065f46"));
                    paint.setTextSize(9);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText(inLabel, 44, boxY1 + 15, paint);
                    paint.setColor(Color.parseColor("#047857"));
                    paint.setTextSize(14f);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText("₹" + String.format(Locale.US, "%,d", totalIn), 44, boxY1 + 33, paint);
                    if (!breakdownText.isEmpty()) {
                        paint.setColor(Color.parseColor("#059669"));
                        paint.setTextSize(6.8f);
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        List<String> bLines = wrapTextLines(breakdownText, paint, boxW - 14);
                        for (int bl = 0; bl < Math.min(2, bLines.size()); bl++) {
                            canvas.drawText(bLines.get(bl), 44, boxY1 + 46 + (bl * 9.5f), paint);
                        }
                    }

                    // Box 2: Total Out / Expenses
                    float box2X = 36 + boxW + 12;
                    paint.setColor(Color.parseColor("#fef2f2"));
                    canvas.drawRoundRect(new RectF(box2X, boxY1, box2X + boxW, boxY2), 6, 6, paint);
                    paint.setColor(Color.parseColor("#fecaca"));
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRoundRect(new RectF(box2X, boxY1, box2X + boxW, boxY2), 6, 6, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.parseColor("#991b1b"));
                    paint.setTextSize(9);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText(outLabel, box2X + 10, boxY1 + 15, paint);
                    paint.setColor(Color.parseColor("#b91c1c"));
                    paint.setTextSize(14f);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText("₹" + String.format(Locale.US, "%,d", totalOut), box2X + 10, boxY1 + 33, paint);
                    paint.setColor(Color.parseColor("#b91c1c"));
                    paint.setTextSize(7.5f);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    canvas.drawText(isExpenseReport ? "सामग्री व व्यवस्था व्यय" : (isEnglish ? "Total Expenditures" : "कुल दर्ज व्यय"), box2X + 10, boxY1 + 50, paint);

                    // Box 3: Net Balance
                    float box3X = box2X + boxW + 12;
                    paint.setColor(Color.parseColor("#eff6ff"));
                    canvas.drawRoundRect(new RectF(box3X, boxY1, 559, boxY2), 6, 6, paint);
                    paint.setColor(Color.parseColor("#bfdbfe"));
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRoundRect(new RectF(box3X, boxY1, 559, boxY2), 6, 6, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.parseColor("#1e40af"));
                    paint.setTextSize(9);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText(netLabel, box3X + 10, boxY1 + 15, paint);
                    paint.setColor(Color.parseColor("#1d4ed8"));
                    paint.setTextSize(14f);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText("₹" + String.format(Locale.US, "%,d", net), box3X + 10, boxY1 + 33, paint);
                    paint.setColor(Color.parseColor("#2563eb"));
                    paint.setTextSize(7.5f);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    canvas.drawText(isExpenseReport ? "शुद्ध उपलब्ध बचत कोष" : (isEnglish ? "Available Balance" : "शुद्ध शेष राशि"), box3X + 10, boxY1 + 50, paint);

                    // 3. Table Header
                    int currentY = 196;
                    drawTableHeader(canvas, paint, currentY, isEnglish, isExpenseReport);
                    currentY += 26;

                    // 4. Rows
                    for (int i = 0; i < transactions.length(); i++) {
                        JSONObject row = transactions.getJSONObject(i);
                        String desc = row.optString("description", "");
                        String date = row.optString("date", "");
                        String method = row.optString("payMethod", "नकद");
                        String spentBy = row.optString("spentBy", "व्यवस्थापक");
                        String typeStr = row.optString("type", "");
                        long amount = row.optLong("amount", 0);
                        boolean isIncome = "contribution".equalsIgnoreCase(typeStr);

                        paint.setTextSize(9.5f);
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        float descColWidth = isExpenseReport ? 190f : 196f;
                        List<String> descLines = wrapTextLines(desc, paint, descColWidth);
                        int lineCount = Math.max(1, descLines.size());
                        int rowHeight = 12 + (lineCount * 14);

                        if (currentY + rowHeight > 780) {
                            drawPageFooter(canvas, paint, pageNumber, isEnglish);
                            document.finishPage(page);

                            pageNumber++;
                            pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                            page = document.startPage(pageInfo);
                            canvas = page.getCanvas();

                            currentY = 45;
                            drawTableHeader(canvas, paint, currentY, isEnglish, isExpenseReport);
                            currentY += 26;
                        }

                        if (i % 2 == 1) {
                            paint.setColor(Color.parseColor("#f8fafc"));
                            canvas.drawRect(36, currentY - 12, 559, currentY + rowHeight - 12, paint);
                        }

                        paint.setColor(Color.parseColor("#e2e8f0"));
                        paint.setStrokeWidth(0.6f);
                        canvas.drawLine(36, currentY + rowHeight - 12, 559, currentY + rowHeight - 12, paint);

                        // S.No
                        paint.setColor(Color.parseColor("#64748b"));
                        paint.setTextSize(9.5f);
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        canvas.drawText(String.valueOf(i + 1), 42, currentY + 2, paint);

                        // Date
                        paint.setColor(Color.parseColor("#334155"));
                        canvas.drawText(date, 66, currentY + 2, paint);

                        // Description lines (wrapped, no truncation)
                        paint.setColor(Color.parseColor("#0f172a"));
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        for (int l = 0; l < descLines.size(); l++) {
                            canvas.drawText(descLines.get(l), 138, currentY + 2 + (l * 14), paint);
                        }

                        if (isExpenseReport) {
                            // Amount (Right-aligned at 400)
                            String amtText = "₹" + String.format(Locale.US, "%,d", amount);
                            paint.setColor(Color.parseColor("#dc2626"));
                            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            float amtW = paint.measureText(amtText);
                            canvas.drawText(amtText, 400 - amtW, currentY + 2, paint);

                            // Spent By
                            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                            paint.setColor(Color.parseColor("#475569"));
                            String sBy = spentBy.length() > 14 ? spentBy.substring(0, 13) + ".." : spentBy;
                            canvas.drawText(sBy, 412, currentY + 2, paint);

                            // Pay Method
                            String pMethod = method.length() > 12 ? method.substring(0, 11) + ".." : method;
                            paint.setColor(Color.parseColor("#2563eb"));
                            canvas.drawText(pMethod, 492, currentY + 2, paint);
                        } else {
                            // Method
                            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                            paint.setColor(Color.parseColor("#475569"));
                            String pMethod = method.length() > 12 ? method.substring(0, 11) + ".." : method;
                            canvas.drawText(pMethod, 350, currentY + 2, paint);

                            // Type
                            String displayType = isIncome ? (isEnglish ? "Deposit" : "जमा") : (isEnglish ? "Expense" : "खर्च");
                            paint.setColor(isIncome ? Color.parseColor("#047857") : Color.parseColor("#b91c1c"));
                            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            canvas.drawText(displayType, 424, currentY + 2, paint);

                            // Amount
                            String amtText = "₹" + String.format(Locale.US, "%,d", amount);
                            paint.setColor(isIncome ? Color.parseColor("#047857") : Color.parseColor("#b91c1c"));
                            float amtW = paint.measureText(amtText);
                            canvas.drawText(amtText, 552 - amtW, currentY + 2, paint);
                        }

                        currentY += rowHeight;
                    }

                    // Table Footer / Summary Row (Expense Report)
                    if (isExpenseReport) {
                        if (currentY + 28 > 780) {
                            drawPageFooter(canvas, paint, pageNumber, isEnglish);
                            document.finishPage(page);

                            pageNumber++;
                            pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                            page = document.startPage(pageInfo);
                            canvas = page.getCanvas();
                            currentY = 45;
                        }

                        paint.setColor(Color.parseColor("#f1f5f9"));
                        canvas.drawRoundRect(new RectF(36, currentY - 12, 559, currentY + 16), 2, 2, paint);
                        paint.setColor(Color.parseColor("#cbd5e1"));
                        paint.setStrokeWidth(1.0f);
                        canvas.drawLine(36, currentY - 12, 559, currentY - 12, paint);
                        canvas.drawLine(36, currentY + 16, 559, currentY + 16, paint);

                        paint.setColor(Color.parseColor("#0f172a"));
                        paint.setTextSize(10f);
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        canvas.drawText(isEnglish ? "TOTAL EXPENSES:" : "कुल खर्च योग:", 138, currentY + 3, paint);

                        String totalAmtText = "₹" + String.format(Locale.US, "%,d", totalOut);
                        paint.setColor(Color.parseColor("#dc2626"));
                        float totalAmtW = paint.measureText(totalAmtText);
                        canvas.drawText(totalAmtText, 400 - totalAmtW, currentY + 3, paint);
                        currentY += 28;
                    }

                    drawPageFooter(canvas, paint, pageNumber, isEnglish);
                    document.finishPage(page);

                    // 5. Save to Mobile Storage (/sdcard/Download)
                    File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!downloadDir.exists()) {
                        downloadDir.mkdirs();
                    }
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                    String prefix = summary.optString("filePrefix", "Yuva_Shakti_BahiKhata_");
                    File pdfFile = new File(downloadDir, prefix + timeStamp + ".pdf");

                    try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                        document.writeTo(fos);
                    }

                    File fixedPdf = new File(downloadDir, prefix.replaceAll("_$", "") + ".pdf");
                    copyFile(pdfFile, fixedPdf);

                    // 6. Media Scan & Download Manager registration
                    MediaScannerConnection.scanFile(
                        MainActivity.this,
                        new String[]{pdfFile.getAbsolutePath(), fixedPdf.getAbsolutePath()},
                        new String[]{"application/pdf", "application/pdf"},
                        null
                    );

                    DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    if (dm != null) {
                        dm.addCompletedDownload(
                            pdfFile.getName(),
                            "युवा शक्ति ग्रुप बही-खाता रिपोर्ट",
                            true,
                            "application/pdf",
                            pdfFile.getAbsolutePath(),
                            pdfFile.length(),
                            true
                        );
                    }

                    Toast.makeText(MainActivity.this, "📄 बही-खाता PDF डाउनलोड फोल्डर में सेव हो गई:\n" + pdfFile.getName(), Toast.LENGTH_LONG).show();

                    // 7. Open with PDF Viewer
                    try {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());

                        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                        viewIntent.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
                        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(viewIntent, isEnglish ? "Open PDF Report" : "बही-खाता PDF खोलें"));
                    } catch (Exception ex) {
                        // User can still open from notification or Downloads folder
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "PDF तैयार करने में त्रुटि: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } finally {
                    if (document != null) {
                        document.close();
                    }
                }
            }
        });
    }

    private List<String> wrapTextLines(String text, Paint paint, float maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            lines.add("-");
            return lines;
        }
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine.append(currentLine.length() == 0 ? word : " " + word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                if (paint.measureText(word) > maxWidth) {
                    StringBuilder subWord = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        char c = word.charAt(i);
                        if (paint.measureText(subWord.toString() + c) <= maxWidth) {
                            subWord.append(c);
                        } else {
                            lines.add(subWord.toString());
                            subWord = new StringBuilder();
                            subWord.append(c);
                        }
                    }
                    if (subWord.length() > 0) {
                        currentLine = subWord;
                    }
                } else {
                    currentLine.append(word);
                }
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    private void drawTableHeader(Canvas canvas, Paint paint, int y, boolean isEnglish, boolean isExpenseReport) {
        paint.setColor(Color.parseColor("#e2e8f0"));
        canvas.drawRoundRect(new RectF(36, y - 14, 559, y + 10), 4, 4, paint);

        paint.setColor(Color.parseColor("#1e293b"));
        paint.setTextSize(9.5f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        if (isExpenseReport) {
            canvas.drawText("#", 42, y, paint);
            canvas.drawText(isEnglish ? "DATE" : "दिनांक", 66, y, paint);
            canvas.drawText(isEnglish ? "ITEM / DESCRIPTION" : "सामग्री / विवरण", 138, y, paint);

            String amtHeader = isEnglish ? "EXPENSE (₹)" : "खर्च (₹)";
            float amtWidth = paint.measureText(amtHeader);
            canvas.drawText(amtHeader, 400 - amtWidth, y, paint);

            canvas.drawText(isEnglish ? "SPENT BY" : "खर्चकर्ता", 412, y, paint);
            canvas.drawText(isEnglish ? "METHOD" : "माध्यम", 492, y, paint);
        } else {
            canvas.drawText("#", 42, y, paint);
            canvas.drawText(isEnglish ? "DATE" : "दिनांक", 66, y, paint);
            canvas.drawText(isEnglish ? "DESCRIPTION / MEMBER" : "विवरण / सदस्य", 138, y, paint);
            canvas.drawText(isEnglish ? "METHOD" : "माध्यम", 350, y, paint);
            canvas.drawText(isEnglish ? "TYPE" : "प्रकार", 424, y, paint);

            String amtHeader = isEnglish ? "AMOUNT" : "राशि (₹)";
            float amtWidth = paint.measureText(amtHeader);
            canvas.drawText(amtHeader, 552 - amtWidth, y, paint);
        }

        paint.setColor(Color.parseColor("#cbd5e1"));
        paint.setStrokeWidth(1.0f);
        canvas.drawLine(36, y + 10, 559, y + 10, paint);
    }

    private void drawPageFooter(Canvas canvas, Paint paint, int pageNumber, boolean isEnglish) {
        paint.setColor(Color.parseColor("#94a3b8"));
        paint.setTextSize(8);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        String footerText = isEnglish ? 
            "Yuva Shakti Group • Kuchera (Nagaur) • Parivar Management" :
            "युवा शक्ति ग्रुप • कुचेरा (नागौर) • परिवार प्रबंधन एवं बही-खाता";
        canvas.drawText(footerText, 42, 820, paint);

        String pageStr = (isEnglish ? "Page " : "पृष्ठ ") + pageNumber;
        float pWidth = paint.measureText(pageStr);
        canvas.drawText(pageStr, 550 - pWidth, 820, paint);
    }

    private void copyFile(File src, File dst) throws Exception {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }

    private void showSystemNotification(String title, String message) {
        try {
            android.app.NotificationManager nm = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return;
            String channelId = "yuva_shakti_alerts_channel";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    channelId,
                    "युवा शक्ति ग्रुप अलर्ट एवं सूचनाएं",
                    android.app.NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("सुविचार, अंशदान स्मरण एवं महत्वपूर्ण सूचनाएं");
                channel.enableVibration(true);
                channel.enableLights(true);
                channel.setLightColor(Color.parseColor("#ea580c"));
                channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
                nm.createNotificationChannel(channel);
            }

            Intent notifyIntent = new Intent(this, MainActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this, (int) System.currentTimeMillis(), notifyIntent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? (android.app.PendingIntent.FLAG_IMMUTABLE | android.app.PendingIntent.FLAG_UPDATE_CURRENT) : android.app.PendingIntent.FLAG_UPDATE_CURRENT
            );

            android.app.Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new android.app.Notification.Builder(this, channelId);
            } else {
                builder = new android.app.Notification.Builder(this);
            }

            builder.setContentTitle(title)
                   .setContentText(message)
                   .setStyle(new android.app.Notification.BigTextStyle().bigText(message))
                   .setSmallIcon(R.mipmap.ic_launcher)
                   .setDefaults(android.app.Notification.DEFAULT_ALL)
                   .setPriority(android.app.Notification.PRIORITY_HIGH)
                   .setAutoCancel(true)
                   .setContentIntent(pendingIntent);

            int notifId = (int) (System.currentTimeMillis() % 1000000);
            nm.notify(notifId, builder.build());
        } catch (Exception ignored) {}
    }

    @Override
    public void onBackPressed() {
        handleBackNavigation();
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
            handleBackNavigation();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handleBackNavigation() {
        if (mWebView != null) {
            try {
                mWebView.evaluateJavascript("(function(){ try { if (typeof window.handleAppBackAction === 'function') { return window.handleAppBackAction() ? 'true' : 'false'; } } catch(e){} return 'false'; })();", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        boolean handled = "true".equalsIgnoreCase(value) || "\"true\"".equalsIgnoreCase(value);
                        if (!handled) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isFinishing() || (Build.VERSION.SDK_INT >= 17 && isDestroyed())) return;
                                    if (mWebView != null && mWebView.canGoBack()) {
                                        mWebView.goBack();
                                        return;
                                    }
                                    handleExitBackPress();
                                }
                            });
                        }
                    }
                });
                return;
            } catch (Exception ignored) {}
        }
        handleExitBackPress();
    }

    private void handleExitBackPress() {
        if (isFinishing() || (Build.VERSION.SDK_INT >= 17 && isDestroyed())) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastBackPressTime < 2000) {
            finish();
        } else {
            mLastBackPressTime = currentTime;
            Toast.makeText(this, "बाहर निकलने के लिए पुनः बैक बटन दबाएं", Toast.LENGTH_SHORT).show();
        }
    }
}
