package com.softdesign.devintensive.data.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.softdesign.devintensive.BuildConfig;
import com.softdesign.devintensive.data.network.interceptors.HeaderInterceptor;
import com.softdesign.devintensive.utils.App;
import com.softdesign.devintensive.utils.AppConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.softdesign.devintensive.utils.AppConfig.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Sergey Vorobyev.
 */

public class ServiceGenerator {

    private static OkHttpClient.Builder sHttpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder sBuilder = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        sHttpClient.addInterceptor(new HeaderInterceptor());
        sHttpClient.addNetworkInterceptor(new StethoInterceptor());
        sHttpClient.addInterceptor(loggingInterceptor);
        sHttpClient.connectTimeout(MAX_CONNECT_TIMEOUT, MILLISECONDS);
        sHttpClient.readTimeout(MAX_READ_TIMEOUT, MILLISECONDS);
        sHttpClient.cache(new Cache(App.get().getCacheDir(), Integer.MAX_VALUE));

        Retrofit retrofit = sBuilder
                .client(sHttpClient.build())
                .build();
        return retrofit.create(serviceClass);
    }
}
