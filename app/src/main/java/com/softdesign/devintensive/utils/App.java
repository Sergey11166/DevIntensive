package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application {

    private static App app;
    private static SharedPreferences sSharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static App get() {
        return app;
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }
}
