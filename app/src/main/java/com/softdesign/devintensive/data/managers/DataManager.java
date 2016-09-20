package com.softdesign.devintensive.data.managers;

import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.request.UserLoginRequest;
import com.softdesign.devintensive.data.network.response.UserModelResponse;

import retrofit2.Call;

/**
 * @author Sergey Vorobyev
 */

public class DataManager {

    private static DataManager instance;

    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DataManager() {
        mPreferencesManager = new PreferencesManager();
        mRestService = ServiceGenerator.createService(RestService.class);
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

    public Call<UserModelResponse> loginUser(UserLoginRequest request) {
        return mRestService.loginUser(request);
    }
}
