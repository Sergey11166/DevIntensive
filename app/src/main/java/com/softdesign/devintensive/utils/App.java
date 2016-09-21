package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App extends Application {

    private static App app;
    private static SharedPreferences sSharedPreferences;
    private static Gson sGson;


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sGson = new GsonBuilder().create();
    }

    public static App get() {
        return app;
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static Gson getGson() {
        return sGson;
    }
}
