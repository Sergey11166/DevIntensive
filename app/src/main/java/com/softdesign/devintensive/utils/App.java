package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.softdesign.devintensive.data.storage.entities.DaoMaster;
import com.softdesign.devintensive.data.storage.entities.DaoSession;

import org.greenrobot.greendao.database.Database;

public class App extends Application {

    private static App app;
    private static SharedPreferences sSharedPreferences;
    private static Gson sGson;
    private static DaoSession sDaoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sGson = new GsonBuilder().create();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "devintensive-db");
        Database database = helper.getWritableDb();
        sDaoSession = new DaoMaster(database).newSession();

        Stetho.initializeWithDefaults(this);
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

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }
}
