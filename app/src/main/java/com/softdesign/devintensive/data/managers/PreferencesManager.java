package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.softdesign.devintensive.utils.App;
import com.softdesign.devintensive.utils.Constants;

import static android.content.SharedPreferences.Editor;
import static com.softdesign.devintensive.utils.Constants.AUTH_TOKEN_KEY;
import static com.softdesign.devintensive.utils.Constants.USER_ID_KEY;

/**
 * Class to manage {@link android.content.SharedPreferences}
 *
 * @author Sergey Vorobyev
 */

public class PreferencesManager {

    private static final String USER_KEY = "USER_KEY";

    private SharedPreferences mPreferences;
    private Gson mGson;

    PreferencesManager() {
        mPreferences = App.getSharedPreferences();
        mGson = App.getGson();
    }

    public void saveUser(User user) {
        Editor editor = mPreferences.edit();
        editor.putString(USER_KEY, mGson.toJson(user));
        editor.apply();
    }

    public User loadUser() {
        String json = mPreferences.getString(USER_KEY, "");
        return mGson.fromJson(json, User.class);
    }

    public void saveAuthToken(String token) {
        Editor editor = mPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY, token);
        editor.apply();
    }

    public void saveUserId(String userId) {
        Editor editor = mPreferences.edit();
        editor.putString(USER_ID_KEY, userId);
        editor.apply();
    }

    public String getAuthToken() {
        return mPreferences.getString(AUTH_TOKEN_KEY, "");
    }

    public String getUserId() {
        return mPreferences.getString(Constants.USER_ID_KEY, "");
    }
}
