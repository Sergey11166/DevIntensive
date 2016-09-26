package com.softdesign.devintensive.data.network;

import com.softdesign.devintensive.data.network.request.UserLoginRequest;
import com.softdesign.devintensive.data.network.response.ImageUploadedResponse;
import com.softdesign.devintensive.data.network.response.UserModelResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * @author Sergey Vorobyev.
 */

public interface RestService {

    @POST("login")
    Call<UserModelResponse> loginUser(@Body UserLoginRequest request);

    @Multipart
    @POST("user/{userId}/publicValues/profilePhoto")
    Call<ImageUploadedResponse> uploadUserPhoto(@Path("userId") String userId, @Part MultipartBody.Part file);

    @Multipart
    @POST("user/{userId}/publicValues/profileAvatar")
    Call<ImageUploadedResponse> uploadAvatar(@Path("userId") String userId, @Part MultipartBody.Part file);
}
