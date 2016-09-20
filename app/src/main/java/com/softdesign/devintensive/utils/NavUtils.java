package com.softdesign.devintensive.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;

import java.io.File;

import static com.softdesign.devintensive.utils.UIUtils.showToast;

/**
 * @author Sergey Vorobyev.
 */

public class NavUtils {

    /**
     * Open system settings of this app
     */
    public static void goToAppSettings(Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Open camera app to take photo
     *
     * @param activity {@link Activity}
     * @param tempFile File to write data from camera
     * @param requestCode Code for {@link Activity#onActivityResult(int, int, Intent)}
     */
    public static void goToCameraApp(Activity activity, File tempFile, int requestCode) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tempFile != null) {
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            activity.startActivityForResult(i, requestCode);
        }
    }

    /**
     * Open gallery app to choose image
     *
     * @param activity {@link Activity}
     * @param requestCode Code for {@link Activity#onActivityResult(int, int, Intent)}
     */
    public static void goToGalleryApp(Activity activity, int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        activity.startActivityForResult(i, requestCode);
    }

    /**
     * Go to url
     *
     * @param context {@link Context}
     * @param url Target url
     */
    public static void goToUrl(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            context.startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            showToast(context, "Browser not found");
        }
    }

    /**
     * Send email
     *
     * @param context {@link Context}
     * @param address Target email addresses
     */
    public static void sendEmail(Context context, String address) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:")); // only email apps should handle this
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{address});
        try {
            context.startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            showToast(context, "Email app not found");
        }
    }

    /**
     * Open phone app
     *
     * @param context {@link Context}
     * @param number Target phone number
     */
    public static void openPhoneApp(Context context, String number) {
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        try {
            context.startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            showToast(context, "Phone app not found");
        }
    }
}
