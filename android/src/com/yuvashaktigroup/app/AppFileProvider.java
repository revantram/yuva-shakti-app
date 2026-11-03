package com.yuvashaktigroup.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * AppFileProvider - Native secure ContentProvider for sharing APK files and images
 * without relying on external AndroidX libraries.
 * Generates valid content:// URIs that modern WhatsApp and Android 10+ accept.
 */
public class AppFileProvider extends ContentProvider {
    public static final String AUTHORITY = "com.yuvashaktigroup.app.fileprovider";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (projection == null) {
            projection = new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE};
        }
        MatrixCursor cursor = new MatrixCursor(projection);
        File file = getFileForUri(uri);
        if (file != null && file.exists()) {
            Object[] row = new Object[projection.length];
            for (int i = 0; i < projection.length; i++) {
                if (OpenableColumns.DISPLAY_NAME.equals(projection[i])) {
                    row[i] = file.getName();
                } else if (OpenableColumns.SIZE.equals(projection[i])) {
                    row[i] = file.length();
                }
            }
            cursor.addRow(row);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        String path = uri.getPath();
        if (path != null && path.endsWith(".apk")) {
            return "application/vnd.android.package-archive";
        } else if (path != null && (path.endsWith(".jpg") || path.endsWith(".jpeg"))) {
            return "image/jpeg";
        } else if (path != null && path.endsWith(".png")) {
            return "image/png";
        } else if (path != null && path.endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        File file = getFileForUri(uri);
        if (file != null && file.exists()) {
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        }
        throw new FileNotFoundException("File not found for URI: " + uri);
    }

    private File getFileForUri(Uri uri) {
        if (getContext() == null) return null;
        String path = uri.getPath();
        if (path == null) return null;
        if (path.startsWith("/")) path = path.substring(1);

        // 1. Check internal cache directory
        File cacheFile = new File(getContext().getCacheDir(), path);
        if (cacheFile.exists()) return cacheFile;

        // 2. Check external cache directory
        try {
            File extCache = getContext().getExternalCacheDir();
            if (extCache != null) {
                File extFile = new File(extCache, path);
                if (extFile.exists()) return extFile;
            }
        } catch (Exception ignored) {}

        // 3. Check internal files directory
        File filesFile = new File(getContext().getFilesDir(), path);
        if (filesFile.exists()) return filesFile;

        // 4. Check external files directory
        try {
            File extFiles = getContext().getExternalFilesDir(null);
            if (extFiles != null) {
                File extFile = new File(extFiles, path);
                if (extFile.exists()) return extFile;
            }
        } catch (Exception ignored) {}

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) { return null; }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }
}
