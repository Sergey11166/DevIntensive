package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.utils.App;
import com.softdesign.devintensive.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.content.SharedPreferences.Editor;
import static com.softdesign.devintensive.utils.Constants.AUTH_TOKEN_KEY;
import static com.softdesign.devintensive.utils.Constants.USER_ID_KEY;

/**
 * Class to manage {@link android.content.SharedPreferences}
 *
 * @author Sergey Vorobyev
 */

public class PreferencesManager {

    private static final String USER_PHONE_KEY = "USER_PHONE_KEY";
    private static final String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    private static final String USER_VK_KEY = "USER_VK_KEY";
    private static final String USER_GIT_KEY = "USER_GIT_KEY";
    private static final String USER_BIO_KEY = "USER_BIO_KEY";
    private static final String USER_PHOTO_KEY = "USER_PHOTO_KEY";

    private static final String[] USER_FIELDS = {
            USER_PHONE_KEY,
            USER_EMAIL_KEY,
            USER_VK_KEY,
            USER_GIT_KEY,USER_BIO_KEY
    };

    private SharedPreferences mPreferences;

    PreferencesManager() {
        mPreferences = App.getSharedPreferences();
    }

    public void saveUserProfileData(List<String> data) {
        Editor editor = mPreferences.edit();
        for (int i = 0 ; i < USER_FIELDS.length; i++) {
            editor.putString(USER_FIELDS[i], data.get(i));
        }
        editor.apply();
    }

    public List<String> loadUserProfileData() {
        List<String> data = new ArrayList<>(5);
        for (String field : USER_FIELDS) data.add(mPreferences.getString(field, null));
        return data;
    }

    public void saveUserPhoto(Uri uri) {
        Editor editor = mPreferences.edit();
        editor.putString(USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }

    public Uri loadUserPhoto() {
        return Uri.parse(mPreferences.getString(USER_PHOTO_KEY, "null"));
    }

    public void saveAuthToken(String token) {
        Editor editor = mPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY, token);
        editor.apply();
    }

    public String getAuthToken() {
        return mPreferences.getString(AUTH_TOKEN_KEY, "null");
    }

    public void saveUserId(int id) {
        Editor editor = mPreferences.edit();
        editor.putInt(Constants.USER_ID_KEY, id);
        editor.apply();
    }

    public int getUserId() {
        return mPreferences.getInt(USER_ID_KEY, 0);
    }
}
