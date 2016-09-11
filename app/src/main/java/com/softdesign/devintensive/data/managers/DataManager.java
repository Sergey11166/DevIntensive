package com.softdesign.devintensive.data.managers;

/**
 * @author Sergey Vorobyev
 */

public class DataManager {

    private static DataManager instance = null;

    private PreferencesManager mPreferencesManager;

    private DataManager() {
        mPreferencesManager = new PreferencesManager();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }
}
