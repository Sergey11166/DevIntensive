package com.softdesign.devintensive.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Sergey Vorobyev
 */

public class IOUtils {

    public static String filePathFromUri(@NonNull Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = App.get().getContentResolver()
                    .query(uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                filePath = cursor.getString(0);
                cursor.close();
            }
        } else {
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * Create image file to save photo when we took photo
     *
     * @return Object of {@link File}
     * @throws IOException
     */
    public static File createImageFile(Context context) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
                .format(System.currentTimeMillis());
        String imageFileName = "JPEG_".concat(timestamp);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, image.getAbsolutePath());
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }
}
