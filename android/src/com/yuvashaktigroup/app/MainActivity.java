package com.yuvashaktigroup.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView mWebView;
    private long mLastBackPressTime = 0;

    public class WebAppInterface {
        @JavascriptInterface
        public void downloadAndInstall(String apkUrl, String versionName) {
            downloadAndInstallApk(apkUrl);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWebView = new WebView(this);
        setContentView(mWebView);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);

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
                if (data != null) {
                    if (data.endsWith(".apk") || data.contains("/releases/download/")) {
                        downloadAndInstallApk(data);
                        return false;
                    }
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                        startActivity(browserIntent);
                    } catch (Exception ignored) {}
                    return false;
                }
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
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

                // Handle WhatsApp links
                if (url.contains("wa.me") || url.contains("api.whatsapp.com") || url.contains("whatsapp://")) {
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

    public void downloadAndInstallApk(final String url) {
        if (url == null || url.trim().isEmpty()) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast.makeText(MainActivity.this, "अपडेट डाउनलोड शुरू हो रहा है...", Toast.LENGTH_SHORT).show();

                    Uri uri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle("युवा शक्ति ग्रुप - नया अपडेट");
                    request.setDescription("नवीनतम APK डाउनलोड हो रहा है...");
                    request.setMimeType("application/vnd.android.package-archive");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "YuvaShaktiGroup.apk");

                    final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    if (manager != null) {
                        final long downloadId = manager.enqueue(request);

                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                                if (id == downloadId) {
                                    try {
                                        Uri apkUri = manager.getUriForDownloadedFile(downloadId);
                                        if (apkUri != null) {
                                            Intent installIntent = new Intent(Intent.ACTION_VIEW);
                                            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            context.startActivity(installIntent);
                                        }
                                    } catch (Exception ex) {
                                        Toast.makeText(MainActivity.this, "डाउनलोड पूर्ण हुआ! डाउनलोड्स फोल्डर से इंस्टॉल करें", Toast.LENGTH_LONG).show();
                                    }
                                    try {
                                        unregisterReceiver(this);
                                    } catch (Exception ignored) {}
                                }
                            }
                        };

                        if (Build.VERSION.SDK_INT >= 33) {
                            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED);
                        } else {
                            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        }
                    }
                } catch (Exception e) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } catch (Exception ex) {
                        Toast.makeText(MainActivity.this, "डाउनलोड त्रुटि: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }

        // Double back press to exit
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastBackPressTime < 2000) {
            super.onBackPressed();
        } else {
            mLastBackPressTime = currentTime;
            Toast.makeText(this, "बाहर निकलने के लिए पुनः बैक बटन दबाएं", Toast.LENGTH_SHORT).show();
        }
    }
}
