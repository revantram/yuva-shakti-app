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
                    Toast.makeText(MainActivity.this, "अपडेट डाउनलोड शुरू हो रहा है...", Toast.LENGTH_SHORT).show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!getPackageManager().canRequestPackageInstalls()) {
                            try {
                                Intent reqIntent = new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                                startActivity(reqIntent);
                                Toast.makeText(MainActivity.this, "कृपया ऐप अपडेट इंस्टॉल की अनुमति दें", Toast.LENGTH_LONG).show();
                            } catch (Exception ignored) {}
                        }
                    }

                    File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (downloadDir != null) {
                        File existingApk = new File(downloadDir, "YuvaShaktiGroup.apk");
                        if (existingApk.exists()) {
                            existingApk.delete();
                        }
                    }

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
                                        File downloadedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "YuvaShaktiGroup.apk");
                                        Intent installIntent = new Intent(Intent.ACTION_VIEW);
                                        if (apkUri != null) {
                                            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                        } else if (downloadedFile.exists()) {
                                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                            StrictMode.setVmPolicy(builder.build());
                                            installIntent.setDataAndType(Uri.fromFile(downloadedFile), "application/vnd.android.package-archive");
                                        }
                                        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        context.startActivity(installIntent);
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
                    long totalIn = summary.optLong("totalIn", 0);
                    long totalOut = summary.optLong("totalOut", 0);
                    long net = summary.optLong("net", totalIn - totalOut);
                    int count = summary.optInt("count", transactions.length());
                    String dateStr = summary.optString("date", "");

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
                    canvas.drawRoundRect(new RectF(36, 36, 559, 105), 8, 8, paint);

                    paint.setColor(Color.WHITE);
                    paint.setTextSize(16);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    String defaultTitle = isEnglish ? "Yuva Shakti Group (Bhati Parivar Kuchera)" : "युवा शक्ति ग्रुप (भाटी परिवार कुचेरा)";
                    String title = summary.optString("title", defaultTitle);
                    canvas.drawText(title, 50, 64, paint);

                    paint.setColor(Color.parseColor("#fbbf24"));
                    paint.setTextSize(11);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    String defaultSub = isEnglish ? "Official Ledger & Financial Statement" : "आधिकारिक बही-खाता एवं आय-व्यय रिपोर्ट";
                    String sub = summary.optString("subTitle", defaultSub);
                    canvas.drawText(sub, 50, 82, paint);

                    paint.setColor(Color.parseColor("#e0e7ff"));
                    paint.setTextSize(9);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    String dateMeta = (isEnglish ? "Report Date: " : "रिपोर्ट दिनांक: ") + dateStr + "  |  " + (isEnglish ? "Total Records: " : "कुल लेनदेन: ") + count;
                    canvas.drawText(dateMeta, 50, 96, paint);

                    // 2. Summary Boxes (3 horizontal cards)
                    float boxY1 = 115;
                    float boxY2 = 160;
                    float boxW = 166;

                    // Box 1: Total In
                    paint.setColor(Color.parseColor("#ecfdf5"));
                    canvas.drawRoundRect(new RectF(36, boxY1, 36 + boxW, boxY2), 6, 6, paint);
                    paint.setColor(Color.parseColor("#a7f3d0"));
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRoundRect(new RectF(36, boxY1, 36 + boxW, boxY2), 6, 6, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.parseColor("#065f46"));
                    paint.setTextSize(9);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    canvas.drawText(isEnglish ? "TOTAL DEPOSITS" : "कुल जमा (अंशदान)", 46, boxY1 + 18, paint);
                    paint.setColor(Color.parseColor("#047857"));
                    paint.setTextSize(14);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText("₹" + String.format(Locale.US, "%,d", totalIn), 46, boxY1 + 36, paint);

                    // Box 2: Total Out
                    float box2X = 36 + boxW + 12;
                    paint.setColor(Color.parseColor("#fef2f2"));
                    canvas.drawRoundRect(new RectF(box2X, boxY1, box2X + boxW, boxY2), 6, 6, paint);
                    paint.setColor(Color.parseColor("#fecaca"));
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRoundRect(new RectF(box2X, boxY1, box2X + boxW, boxY2), 6, 6, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.parseColor("#991b1b"));
                    paint.setTextSize(9);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    canvas.drawText(isEnglish ? "TOTAL EXPENSES" : "कुल खर्च (व्यय)", box2X + 10, boxY1 + 18, paint);
                    paint.setColor(Color.parseColor("#b91c1c"));
                    paint.setTextSize(14);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText("₹" + String.format(Locale.US, "%,d", totalOut), box2X + 10, boxY1 + 36, paint);

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
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    canvas.drawText(isEnglish ? "NET BALANCE" : "उपलब्ध शेष राशि", box3X + 10, boxY1 + 18, paint);
                    paint.setColor(Color.parseColor("#1d4ed8"));
                    paint.setTextSize(14);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText("₹" + String.format(Locale.US, "%,d", net), box3X + 10, boxY1 + 36, paint);

                    // 3. Table Header
                    int currentY = 175;
                    drawTableHeader(canvas, paint, currentY, isEnglish);
                    currentY += 24;

                    // 4. Rows
                    for (int i = 0; i < transactions.length(); i++) {
                        if (currentY > 790) {
                            drawPageFooter(canvas, paint, pageNumber, isEnglish);
                            document.finishPage(page);

                            pageNumber++;
                            pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                            page = document.startPage(pageInfo);
                            canvas = page.getCanvas();

                            currentY = 45;
                            drawTableHeader(canvas, paint, currentY, isEnglish);
                            currentY += 24;
                        }

                        JSONObject row = transactions.getJSONObject(i);
                        String desc = row.optString("description", "");
                        String date = row.optString("date", "");
                        String method = row.optString("payMethod", "");
                        String typeStr = row.optString("type", "");
                        long amount = row.optLong("amount", 0);
                        boolean isIncome = "contribution".equalsIgnoreCase(typeStr);

                        if (i % 2 == 1) {
                            paint.setColor(Color.parseColor("#f8fafc"));
                            canvas.drawRect(36, currentY - 14, 559, currentY + 6, paint);
                        }

                        paint.setColor(Color.parseColor("#e2e8f0"));
                        paint.setStrokeWidth(0.5f);
                        canvas.drawLine(36, currentY + 6, 559, currentY + 6, paint);

                        paint.setColor(Color.parseColor("#1e293b"));
                        paint.setTextSize(9);
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        String truncatedDesc = desc.length() > 32 ? desc.substring(0, 30) + "..." : desc;
                        canvas.drawText(truncatedDesc, 42, currentY, paint);

                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        paint.setColor(Color.parseColor("#475569"));
                        canvas.drawText(date, 245, currentY, paint);

                        String truncatedMethod = method.length() > 14 ? method.substring(0, 12) + "..." : method;
                        canvas.drawText(truncatedMethod, 325, currentY, paint);

                        String displayType = isIncome ? (isEnglish ? "Deposit" : "जमा") : (isEnglish ? "Expense" : "खर्च");
                        paint.setColor(isIncome ? Color.parseColor("#047857") : Color.parseColor("#b91c1c"));
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        canvas.drawText(displayType, 410, currentY, paint);

                        String amtText = "₹" + String.format(Locale.US, "%,d", amount);
                        paint.setColor(isIncome ? Color.parseColor("#047857") : Color.parseColor("#b91c1c"));
                        float amtWidth = paint.measureText(amtText);
                        canvas.drawText(amtText, 550 - amtWidth, currentY, paint);

                        currentY += 20;
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

    private void drawTableHeader(Canvas canvas, Paint paint, int y, boolean isEnglish) {
        paint.setColor(Color.parseColor("#f1f5f9"));
        canvas.drawRect(36, y - 14, 559, y + 8, paint);

        paint.setColor(Color.parseColor("#334155"));
        paint.setTextSize(9);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText(isEnglish ? "DESCRIPTION / MEMBER" : "विवरण / सदस्य", 42, y, paint);
        canvas.drawText(isEnglish ? "DATE" : "तारीख", 245, y, paint);
        canvas.drawText(isEnglish ? "METHOD" : "माध्यम", 325, y, paint);
        canvas.drawText(isEnglish ? "TYPE" : "प्रकार", 410, y, paint);

        String amtHeader = isEnglish ? "AMOUNT" : "राशि";
        float amtWidth = paint.measureText(amtHeader);
        canvas.drawText(amtHeader, 550 - amtWidth, y, paint);

        paint.setColor(Color.parseColor("#cbd5e1"));
        paint.setStrokeWidth(1.0f);
        canvas.drawLine(36, y + 8, 559, y + 8, paint);
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
