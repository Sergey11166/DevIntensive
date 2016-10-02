package com.softdesign.devintensive.data.managers;

import com.softdesign.devintensive.data.network.PicassoCache;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.request.UserLoginRequest;
import com.softdesign.devintensive.data.network.response.AuthResponse;
import com.softdesign.devintensive.data.network.response.ImageUploadedResponse;
import com.softdesign.devintensive.data.network.response.UserListResponse;
import com.softdesign.devintensive.data.network.restmodels.User;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * @author Sergey Vorobyev
 */

public class DataManager {

    private static DataManager instance;
    private Picasso mPicasso;

    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DataManager() {
        mPreferencesManager = new PreferencesManager();
        mRestService = ServiceGenerator.createService(RestService.class);
        mPicasso = new PicassoCache().getPicassoInstance();
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

    public Picasso getPicasso() {
        return mPicasso;
    }

    public Call<AuthResponse> loginUser(UserLoginRequest request) {
        return mRestService.loginUser(request);
    }

    public Call<UserListResponse> getUserList() {
        return mRestService.getUserList();
    }

    public Call<ImageUploadedResponse> uploadUserPhoto(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        String fileName = String.valueOf(System.currentTimeMillis()) + "_" + "user_photo.jpg";
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestFile);
        User user = mPreferencesManager.loadUser();
        return mRestService.uploadUserPhoto(user.getId(), body);
    }

    public Call<ImageUploadedResponse> uploadAvatar(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        String fileName = String.valueOf(System.currentTimeMillis()) + "_" + "avatar.jpg";
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestFile);
        User user = mPreferencesManager.loadUser();
        return mRestService.uploadAvatar(user.getId(), body);
    }
}
