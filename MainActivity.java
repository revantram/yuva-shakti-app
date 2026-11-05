package com.yuvashaktigroup.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.util.Base64;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

/* loaded from: classes.dex */
public class MainActivity extends Activity {
    private static final int FILECHOOSER_RESULTCODE = 1001;
    private ValueCallback<Uri[]> mFilePathCallback;
    private long mLastBackPressTime = 0;
    private WebView mWebView;

    /* compiled from: D8$$SyntheticClass */
    /* renamed from: com.yuvashaktigroup.app.MainActivity$0, reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class AnonymousClass0 {
        public static /* synthetic */ void m(Throwable th, Throwable th2) {
            try {
                Throwable.class.getDeclaredMethod("addSuppressed", Throwable.class).invoke(th, th2);
            } catch (Exception e) {
            }
        }
    }

    /* loaded from: classes.dex */
    public class WebAppInterface {
        public WebAppInterface() {
        }

        @JavascriptInterface
        public void downloadAndInstall(String str, String str2) {
            MainActivity.this.downloadAndInstallApk(str);
        }

        @JavascriptInterface
        public void shareAppApk() {
            MainActivity.this.shareApplicationApk();
        }

        @JavascriptInterface
        public void shareImage(String str, String str2) {
            MainActivity.this.shareGeneratedImage(str, str2);
        }

        @JavascriptInterface
        public void printLedgerPdf() {
            MainActivity.this.printLedgerReport();
        }

        @JavascriptInterface
        public void saveLedgerPdf(String str, String str2) {
            MainActivity.this.generateAndSaveLedgerPdf(str, str2);
        }

        @JavascriptInterface
        public void openWhatsApp(String str) {
            try {
                MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "व्हाट्सएप उपलब्ध नहीं है", 0).show();
            }
        }

        @JavascriptInterface
        public void shareText(String str, String str2) {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", str);
                MainActivity mainActivity = MainActivity.this;
                if (str2 == null) {
                    str2 = "शेयर करें";
                }
                mainActivity.startActivity(Intent.createChooser(intent, str2));
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "शेयर करने में त्रुटि", 0).show();
            }
        }

        @JavascriptInterface
        public void postNotification(String str, String str2) {
            MainActivity.this.showSystemNotification(str, str2);
        }

        @JavascriptInterface
        public void requestNotificationPermission() {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.yuvashaktigroup.app.MainActivity.WebAppInterface.1
                @Override // java.lang.Runnable
                public void run() {
                    MainActivity.this.checkAndRequestNotificationPermission();
                }
            });
        }

        @JavascriptInterface
        public boolean hasNotificationPermission() {
            return Build.VERSION.SDK_INT < 33 || MainActivity.this.checkSelfPermission("android.permission.POST_NOTIFICATIONS") == 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS") != 0) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 200);
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        checkAndRequestNotificationPermission();
        this.mWebView = new WebView(this);
        setContentView(this.mWebView);
        WebSettings settings = this.mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(-1);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setUserAgentString(settings.getUserAgentString().replace("; wv", "").replaceAll("Version/\\d+\\.\\d+\\s*", ""));
        try {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.mWebView, true);
        } catch (Exception e) {
        }
        this.mWebView.addJavascriptInterface(new WebAppInterface(), "AndroidApp");
        this.mWebView.setDownloadListener(new DownloadListener() { // from class: com.yuvashaktigroup.app.MainActivity.1
            @Override // android.webkit.DownloadListener
            public void onDownloadStart(String str, String str2, String str3, String str4, long j) {
                MainActivity.this.downloadAndInstallApk(str);
            }
        });
        this.mWebView.setWebChromeClient(new WebChromeClient() { // from class: com.yuvashaktigroup.app.MainActivity.2
            @Override // android.webkit.WebChromeClient
            public boolean onCreateWindow(WebView webView, boolean z, boolean z2, Message message) {
                WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                String extra = hitTestResult != null ? hitTestResult.getExtra() : null;
                if (extra != null) {
                    if (extra.endsWith(".apk") || extra.contains("/releases/download/")) {
                        MainActivity.this.downloadAndInstallApk(extra);
                        return false;
                    }
                    try {
                        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(extra)));
                    } catch (Exception e2) {
                    }
                    return false;
                }
                return super.onCreateWindow(webView, z, z2, message);
            }

            @Override // android.webkit.WebChromeClient
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (MainActivity.this.mFilePathCallback != null) {
                    MainActivity.this.mFilePathCallback.onReceiveValue(null);
                }
                MainActivity.this.mFilePathCallback = valueCallback;
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("image/*");
                Intent intent2 = new Intent("android.intent.action.CHOOSER");
                intent2.putExtra("android.intent.extra.INTENT", intent);
                intent2.putExtra("android.intent.extra.TITLE", "प्रोफाइल फोटो चुनें");
                try {
                    MainActivity.this.startActivityForResult(intent2, MainActivity.FILECHOOSER_RESULTCODE);
                    return true;
                } catch (Exception e2) {
                    MainActivity.this.mFilePathCallback = null;
                    return false;
                }
            }
        });
        this.mWebView.setWebViewClient(new WebViewClient() { // from class: com.yuvashaktigroup.app.MainActivity.3
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                if (str == null) {
                    return false;
                }
                if (str.startsWith("upi://") || str.startsWith("phonepe://")) {
                    try {
                        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
                        return true;
                    } catch (Exception e2) {
                        Toast.makeText(MainActivity.this, "PhonePe / UPI ऐप नहीं मिला", 0).show();
                        return true;
                    }
                }
                if (str.startsWith("intent://")) {
                    try {
                        Intent parseUri = Intent.parseUri(str, 1);
                        if (parseUri != null) {
                            if (MainActivity.this.getPackageManager().resolveActivity(parseUri, 0) != null) {
                                MainActivity.this.startActivity(parseUri);
                                return true;
                            }
                            String stringExtra = parseUri.getStringExtra("browser_fallback_url");
                            if (stringExtra != null) {
                                webView.loadUrl(stringExtra);
                                return true;
                            }
                        }
                    } catch (Exception e3) {
                        Toast.makeText(MainActivity.this, "ऐप खोलने में असमर्थ", 0).show();
                        return true;
                    }
                }
                if (str.endsWith(".apk") || str.contains("/releases/download/")) {
                    MainActivity.this.downloadAndInstallApk(str);
                    return true;
                }
                if (str.startsWith("tel:")) {
                    try {
                        MainActivity.this.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(str)));
                        return true;
                    } catch (Exception e4) {
                        Toast.makeText(MainActivity.this, "कॉल करने में असमर्थ", 0).show();
                        return true;
                    }
                }
                if (str.contains("chat.whatsapp.com") || str.contains("wa.me") || str.contains("api.whatsapp.com") || str.contains("whatsapp://")) {
                    try {
                        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
                        return true;
                    } catch (Exception e5) {
                        Toast.makeText(MainActivity.this, "व्हाट्सएप उपलब्ध नहीं है", 0).show();
                        return true;
                    }
                }
                if (str.contains("instagram.com")) {
                    try {
                        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
                        return true;
                    } catch (Exception e6) {
                        Toast.makeText(MainActivity.this, "इंस्टाग्राम खोलने में असमर्थ", 0).show();
                        return true;
                    }
                }
                if (str.startsWith("file:") || str.startsWith("http:") || str.startsWith("https:")) {
                    return false;
                }
                try {
                    MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
                    return true;
                } catch (Exception e7) {
                    return false;
                }
            }
        });
        this.mWebView.loadUrl("file:///android_asset/index.html");
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == FILECHOOSER_RESULTCODE) {
            if (this.mFilePathCallback != null) {
                this.mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(i2, intent));
                this.mFilePathCallback = null;
                return;
            }
            return;
        }
        super.onActivityResult(i, i2, intent);
    }

    public void downloadAndInstallApk(final String str) {
        if (str == null || str.trim().isEmpty()) {
            return;
        }
        runOnUiThread(new Runnable() { // from class: com.yuvashaktigroup.app.MainActivity.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Toast.makeText(MainActivity.this, "अपडेट डाउनलोड शुरू हो रहा है...", 0).show();
                    if (Build.VERSION.SDK_INT >= 26 && !MainActivity.this.getPackageManager().canRequestPackageInstalls()) {
                        try {
                            MainActivity.this.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse("package:" + MainActivity.this.getPackageName())));
                            Toast.makeText(MainActivity.this, "कृपया ऐप अपडेट इंस्टॉल की अनुमति दें", 1).show();
                        } catch (Exception e) {
                        }
                    }
                    File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (externalStoragePublicDirectory != null) {
                        File file = new File(externalStoragePublicDirectory, "YuvaShaktiGroup.apk");
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str));
                    request.setTitle("युवा शक्ति ग्रुप - नया अपडेट");
                    request.setDescription("नवीनतम APK डाउनलोड हो रहा है...");
                    request.setMimeType("application/vnd.android.package-archive");
                    request.setNotificationVisibility(1);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "YuvaShaktiGroup.apk");
                    final DownloadManager downloadManager = (DownloadManager) MainActivity.this.getSystemService("download");
                    if (downloadManager != null) {
                        final long enqueue = downloadManager.enqueue(request);
                        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.yuvashaktigroup.app.MainActivity.4.1
                            @Override // android.content.BroadcastReceiver
                            public void onReceive(Context context, Intent intent) {
                                if (intent.getLongExtra("extra_download_id", -1L) == enqueue) {
                                    try {
                                        Uri uriForDownloadedFile = downloadManager.getUriForDownloadedFile(enqueue);
                                        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "YuvaShaktiGroup.apk");
                                        Intent intent2 = new Intent("android.intent.action.VIEW");
                                        if (uriForDownloadedFile != null) {
                                            intent2.setDataAndType(uriForDownloadedFile, "application/vnd.android.package-archive");
                                        } else if (file2.exists()) {
                                            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
                                            intent2.setDataAndType(Uri.fromFile(file2), "application/vnd.android.package-archive");
                                        }
                                        intent2.setFlags(268435457);
                                        context.startActivity(intent2);
                                    } catch (Exception e2) {
                                        Toast.makeText(MainActivity.this, "डाउनलोड पूर्ण हुआ! डाउनलोड्स फोल्डर से इंस्टॉल करें", 1).show();
                                    }
                                    try {
                                        MainActivity.this.unregisterReceiver(this);
                                    } catch (Exception e3) {
                                    }
                                }
                            }
                        };
                        int i = Build.VERSION.SDK_INT;
                        MainActivity mainActivity = MainActivity.this;
                        if (i >= 33) {
                            mainActivity.registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"), 2);
                        } else {
                            mainActivity.registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
                        }
                    }
                } catch (Exception e2) {
                    try {
                        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
                    } catch (Exception e3) {
                        Toast.makeText(MainActivity.this, "डाउनलोड त्रुटि: " + e3.getMessage(), 0).show();
                    }
                }
            }
        });
    }

    public void shareApplicationApk() {
        runOnUiThread(new Runnable() { // from class: com.yuvashaktigroup.app.MainActivity.5
            @Override // java.lang.Runnable
            public void run() {
                Toast.makeText(MainActivity.this, "📦 एप्लिकेशन APK तैयार हो रही है...", 0).show();
            }
        });
        new Thread(new Runnable() { // from class: com.yuvashaktigroup.app.MainActivity.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    File file = new File(MainActivity.this.getPackageCodePath());
                    if (!file.exists() || file.length() == 0) {
                        file = new File(MainActivity.this.getApplicationInfo().sourceDir);
                    }
                    File file2 = new File(MainActivity.this.getCacheDir(), "shared_apk");
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    File file3 = new File(file2, "YuvaShaktiGroup.apk");
                    if (!file3.exists() || file3.length() != file.length()) {
                        MainActivity.this.copyFile(file, file3);
                    }
                    try {
                        File externalCacheDir = MainActivity.this.getExternalCacheDir();
                        if (externalCacheDir != null) {
                            File file4 = new File(externalCacheDir, "shared_apk");
                            if (!file4.exists()) {
                                file4.mkdirs();
                            }
                            File file5 = new File(file4, "YuvaShaktiGroup.apk");
                            if (!file5.exists() || file5.length() != file.length()) {
                                MainActivity.this.copyFile(file, file5);
                            }
                        }
                    } catch (Exception e) {
                    }
                    try {
                        File file6 = new File("/sdcard/Download/YuvaShaktiGroup.apk");
                        if (!file6.exists() || file6.length() != file.length()) {
                            MainActivity.this.copyFile(file, file6);
                        }
                    } catch (Exception e2) {
                    }
                    final Uri parse = Uri.parse("content://com.yuvashaktigroup.app.fileprovider/shared_apk/YuvaShaktiGroup.apk");
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: com.yuvashaktigroup.app.MainActivity.6.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                Intent intent = new Intent("android.intent.action.SEND");
                                intent.setType("application/vnd.android.package-archive");
                                intent.putExtra("android.intent.extra.STREAM", parse);
                                intent.putExtra("android.intent.extra.TEXT", "🚩 *युवा शक्ति ग्रुप (भाटी परिवार • कुचेरा)* 🚩\n\nआधिकारिक परिवार प्रबंधन एवं बही-खाता एप्लिकेशन (APK फाइल)।\n\n📲 *सीधा APK डाउनलोड लिंक:*\nhttps://github.com/revantram/yuva-shakti-app/releases/latest/download/YuvaShaktiGroup.apk\n\n💬 *व्हाट्सएप ग्रुप लिंक:*\nhttps://chat.whatsapp.com/JOVKSohSoBHEokrCXOSkTh");
                                intent.putExtra("android.intent.extra.SUBJECT", "युवा शक्ति ग्रुप ऐप APK");
                                intent.setClipData(ClipData.newRawUri("YuvaShaktiGroup.apk", parse));
                                intent.addFlags(1);
                                Iterator<ResolveInfo> it = MainActivity.this.getPackageManager().queryIntentActivities(intent, 65536).iterator();
                                while (it.hasNext()) {
                                    MainActivity.this.grantUriPermission(it.next().activityInfo.packageName, parse, 1);
                                }
                                boolean z = false;
                                String[] strArr = {"com.whatsapp", "com.whatsapp.w4b"};
                                int i = 0;
                                while (true) {
                                    if (i >= 2) {
                                        break;
                                    }
                                    String str = strArr[i];
                                    try {
                                        MainActivity.this.getPackageManager().getPackageInfo(str, 0);
                                        intent.setPackage(str);
                                        MainActivity.this.grantUriPermission(str, parse, 1);
                                        z = true;
                                        break;
                                    } catch (Exception e3) {
                                        i++;
                                    }
                                }
                                if (!z) {
                                    intent.setPackage(null);
                                }
                                try {
                                    MainActivity.this.startActivity(intent);
                                } catch (Exception e4) {
                                    intent.setPackage(null);
                                    Intent createChooser = Intent.createChooser(intent, "युवा शक्ति ऐप APK शेयर करें");
                                    createChooser.addFlags(1);
                                    MainActivity.this.startActivity(createChooser);
                                }
                            } catch (Exception e5) {
                                Toast.makeText(MainActivity.this, "शेयर त्रुटि: " + e5.getMessage(), 1).show();
                            }
                        }
                    });
                } catch (Exception e3) {
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: com.yuvashaktigroup.app.MainActivity.6.2
                        @Override // java.lang.Runnable
                        public void run() {
                            Toast.makeText(MainActivity.this, "APK तैयार करने में त्रुटि: " + e3.getMessage(), 1).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void shareGeneratedImage(final String str, final String str2) {
        runOnUiThread(new Runnable() { // from class: com.yuvashaktigroup.app.MainActivity.7
            @Override // java.lang.Runnable
            public void run() {
                boolean z;
                try {
                    String str3 = str;
                    if (str3.contains(",")) {
                        str3 = str3.substring(str3.indexOf(",") + 1);
                    }
                    byte[] decode = Base64.decode(str3, 0);
                    try {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YuvaShakti");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        File file2 = new File(file, "suvichar_" + System.currentTimeMillis() + ".jpg");
                        FileOutputStream fileOutputStream = new FileOutputStream(file2);
                        fileOutputStream.write(decode);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        MediaScannerConnection.scanFile(MainActivity.this, new String[]{file2.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
                    } catch (Exception e) {
                    }
                    File file3 = new File(MainActivity.this.getCacheDir(), "shared_images");
                    if (!file3.exists()) {
                        file3.mkdirs();
                    }
                    File file4 = new File(file3, "suvichar_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream fileOutputStream2 = new FileOutputStream(file4);
                    fileOutputStream2.write(decode);
                    fileOutputStream2.flush();
                    fileOutputStream2.close();
                    Uri parse = Uri.parse("content://com.yuvashaktigroup.app.fileprovider/shared_images/" + file4.getName());
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("image/jpeg");
                    intent.putExtra("android.intent.extra.STREAM", parse);
                    if (str2 != null && !str2.isEmpty()) {
                        intent.putExtra("android.intent.extra.TEXT", str2);
                    }
                    intent.setClipData(ClipData.newRawUri("suvichar.jpg", parse));
                    intent.addFlags(1);
                    Iterator<ResolveInfo> it = MainActivity.this.getPackageManager().queryIntentActivities(intent, 65536).iterator();
                    while (it.hasNext()) {
                        MainActivity.this.grantUriPermission(it.next().activityInfo.packageName, parse, 1);
                    }
                    String[] strArr = {"com.whatsapp", "com.whatsapp.w4b"};
                    int i = 0;
                    while (true) {
                        if (i >= 2) {
                            z = false;
                            break;
                        }
                        String str4 = strArr[i];
                        try {
                            MainActivity.this.getPackageManager().getPackageInfo(str4, 0);
                            intent.setPackage(str4);
                            MainActivity.this.grantUriPermission(str4, parse, 1);
                            z = true;
                            break;
                        } catch (Exception e2) {
                            i++;
                        }
                    }
                    if (!z) {
                        intent.setPackage(null);
                    }
                    try {
                        MainActivity.this.startActivity(intent);
                    } catch (Exception e3) {
                        intent.setPackage(null);
                        Intent createChooser = Intent.createChooser(intent, "सुविचार फोटो शेयर करें");
                        createChooser.addFlags(1);
                        MainActivity.this.startActivity(createChooser);
                    }
                } catch (Exception e4) {
                    Toast.makeText(MainActivity.this, "फोटो शेयर करने में त्रुटि: " + e4.getMessage(), 0).show();
                }
            }
        });
    }

    public void printLedgerReport() {
        runOnUiThread(new Runnable() { // from class: com.yuvashaktigroup.app.MainActivity.8
            @Override // java.lang.Runnable
            public void run() {
                try {
                    PrintManager printManager = (PrintManager) MainActivity.this.getSystemService("print");
                    if (printManager != null) {
                        printManager.print("युवा शक्ति ग्रुप - बही-खाता रिपोर्ट", MainActivity.this.mWebView.createPrintDocumentAdapter("युवा शक्ति ग्रुप - बही-खाता रिपोर्ट"), new PrintAttributes.Builder().build());
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "प्रिंट / पीडीएफ तैयार करने में असमर्थ: " + e.getMessage(), 0).show();
                }
            }
        });
    }

    public void generateAndSaveLedgerPdf(final String str, final String str2) {
        runOnUiThread(new Runnable() { // from class: com.yuvashaktigroup.app.MainActivity.9
            /* JADX WARN: Removed duplicated region for block: B:109:0x0657  */
            /* JADX WARN: Removed duplicated region for block: B:116:0x0650  */
            /* JADX WARN: Removed duplicated region for block: B:118:? A[RETURN, SYNTHETIC] */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void run() {
                /*
                    Method dump skipped, instructions count: 1629
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: com.yuvashaktigroup.app.MainActivity.AnonymousClass9.run():void");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drawTableHeader(Canvas canvas, Paint paint, int i, boolean z) {
        paint.setColor(Color.parseColor("#f1f5f9"));
        float f = i + 8;
        canvas.drawRect(36.0f, i - 14, 559.0f, f, paint);
        paint.setColor(Color.parseColor("#334155"));
        paint.setTextSize(9.0f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, 1));
        float f2 = i;
        canvas.drawText(z ? "DESCRIPTION / MEMBER" : "विवरण / सदस्य", 42.0f, f2, paint);
        canvas.drawText(z ? "DATE" : "तारीख", 245.0f, f2, paint);
        canvas.drawText(z ? "METHOD" : "माध्यम", 325.0f, f2, paint);
        canvas.drawText(z ? "TYPE" : "प्रकार", 410.0f, f2, paint);
        String str = z ? "AMOUNT" : "राशि";
        canvas.drawText(str, 550.0f - paint.measureText(str), f2, paint);
        paint.setColor(Color.parseColor("#cbd5e1"));
        paint.setStrokeWidth(1.0f);
        canvas.drawLine(36.0f, f, 559.0f, f, paint);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drawPageFooter(Canvas canvas, Paint paint, int i, boolean z) {
        String str;
        paint.setColor(Color.parseColor("#94a3b8"));
        paint.setTextSize(8.0f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, 0));
        if (z) {
            str = "Yuva Shakti Group • Kuchera (Nagaur) • Parivar Management";
        } else {
            str = "युवा शक्ति ग्रुप • कुचेरा (नागौर) • परिवार प्रबंधन एवं बही-खाता";
        }
        canvas.drawText(str, 42.0f, 820.0f, paint);
        String str2 = (z ? "Page " : "पृष्ठ ") + i;
        canvas.drawText(str2, 550.0f - paint.measureText(str2), 820.0f, paint);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void copyFile(File file, File file2) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            try {
                byte[] bArr = new byte[8192];
                while (true) {
                    int read = fileInputStream.read(bArr);
                    if (read > 0) {
                        fileOutputStream.write(bArr, 0, read);
                    } else {
                        fileOutputStream.close();
                        fileInputStream.close();
                        return;
                    }
                }
            } finally {
            }
        } catch (Throwable th) {
            try {
                fileInputStream.close();
            } catch (Throwable th2) {
                AnonymousClass0.m(th, th2);
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSystemNotification(String str, String str2) {
        Notification.Builder builder;
        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
            if (notificationManager == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel notificationChannel = new NotificationChannel("yuva_shakti_alerts_channel", "युवा शक्ति ग्रुप अलर्ट एवं सूचनाएं", 4);
                notificationChannel.setDescription("सुविचार, अंशदान स्मरण एवं महत्वपूर्ण सूचनाएं");
                notificationChannel.enableVibration(true);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.parseColor("#ea580c"));
                notificationChannel.setLockscreenVisibility(1);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            Intent intent = new Intent(this, (Class<?>) MainActivity.class);
            intent.setFlags(603979776);
            PendingIntent activity = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, Build.VERSION.SDK_INT >= 23 ? 201326592 : 134217728);
            if (Build.VERSION.SDK_INT >= 26) {
                builder = new Notification.Builder(this, "yuva_shakti_alerts_channel");
            } else {
                builder = new Notification.Builder(this);
            }
            builder.setContentTitle(str).setContentText(str2).setStyle(new Notification.BigTextStyle().bigText(str2)).setSmallIcon(R.mipmap.ic_launcher).setDefaults(-1).setPriority(1).setAutoCancel(true).setContentIntent(activity);
            notificationManager.notify((int) (System.currentTimeMillis() % 1000000), builder.build());
        } catch (Exception e) {
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.mWebView != null && this.mWebView.canGoBack()) {
            this.mWebView.goBack();
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.mLastBackPressTime < 2000) {
            super.onBackPressed();
        } else {
            this.mLastBackPressTime = currentTimeMillis;
            Toast.makeText(this, "बाहर निकलने के लिए पुनः बैक बटन दबाएं", 0).show();
        }
    }
}
