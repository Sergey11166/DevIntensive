package com.softdesign.devintensive.utils;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

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
}
