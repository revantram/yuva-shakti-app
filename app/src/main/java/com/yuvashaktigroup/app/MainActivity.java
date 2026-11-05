package com.yuvashaktigroup.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;

/* loaded from: classes.dex */
public class AppFileProvider extends ContentProvider {
    public static final String AUTHORITY = "com.yuvashaktigroup.app.fileprovider";

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        if (strArr == null) {
            strArr = new String[]{"_display_name", "_size"};
        }
        MatrixCursor matrixCursor = new MatrixCursor(strArr);
        File fileForUri = getFileForUri(uri);
        if (fileForUri != null && fileForUri.exists()) {
            Object[] objArr = new Object[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                if (!"_display_name".equals(strArr[i])) {
                    if ("_size".equals(strArr[i])) {
                        objArr[i] = Long.valueOf(fileForUri.length());
                    }
                } else {
                    objArr[i] = fileForUri.getName();
                }
            }
            matrixCursor.addRow(objArr);
        }
        return matrixCursor;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        String path = uri.getPath();
        if (path != null && path.endsWith(".apk")) {
            return "application/vnd.android.package-archive";
        }
        if (path != null && (path.endsWith(".jpg") || path.endsWith(".jpeg"))) {
            return "image/jpeg";
        }
        if (path != null && path.endsWith(".png")) {
            return "image/png";
        }
        if (path != null && path.endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        File fileForUri = getFileForUri(uri);
        if (fileForUri != null && fileForUri.exists()) {
            return ParcelFileDescriptor.open(fileForUri, 268435456);
        }
        throw new FileNotFoundException("File not found for URI: " + uri);
    }

    private File getFileForUri(Uri uri) {
        String path;
        if (getContext() == null || (path = uri.getPath()) == null) {
            return null;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        File file = new File(getContext().getCacheDir(), path);
        if (file.exists()) {
            return file;
        }
        try {
            File externalCacheDir = getContext().getExternalCacheDir();
            if (externalCacheDir != null) {
                File file2 = new File(externalCacheDir, path);
                if (file2.exists()) {
                    return file2;
                }
            }
        } catch (Exception e) {
        }
        File file3 = new File(getContext().getFilesDir(), path);
        if (file3.exists()) {
            return file3;
        }
        try {
            File externalFilesDir = getContext().getExternalFilesDir(null);
            if (externalFilesDir != null) {
                File file4 = new File(externalFilesDir, path);
                if (file4.exists()) {
                    return file4;
                }
            }
        } catch (Exception e2) {
        }
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }
}
