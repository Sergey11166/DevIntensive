package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.AbstractDaoSession;

public class App extends Application {

    private static App app;
    private static SharedPreferences sSharedPreferences;
    private static Gson sGson;


    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
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
