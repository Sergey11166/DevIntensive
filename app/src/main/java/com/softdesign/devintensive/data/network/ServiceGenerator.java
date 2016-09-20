package com.softdesign.devintensive.data.network;

import com.softdesign.devintensive.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Sergey Vorobyev.
 */

public class ServiceGenerator {

    private static OkHttpClient.Builder sHttpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder sBuilder = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        sHttpClient.addInterceptor(interceptor);

        Retrofit retrofit = sBuilder
                .client(sHttpClient.build())
                .build();
        return retrofit.create(serviceClass);
    }
}
