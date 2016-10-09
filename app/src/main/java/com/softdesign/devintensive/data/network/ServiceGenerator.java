package com.softdesign.devintensive.data.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.softdesign.devintensive.BuildConfig;
import com.softdesign.devintensive.data.network.interceptors.HeaderInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.softdesign.devintensive.utils.AppConfig.MAX_CONNECT_TIMEOUT;
import static com.softdesign.devintensive.utils.AppConfig.MAX_READ_TIMEOUT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author Sergey Vorobyev.
 */

public class ServiceGenerator {

    private static OkHttpClient.Builder sHttpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder sBuilder = new Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        sHttpClient.addInterceptor(new HeaderInterceptor());
        sHttpClient.addNetworkInterceptor(new StethoInterceptor());
        sHttpClient.addInterceptor(loggingInterceptor);
        sHttpClient.connectTimeout(MAX_CONNECT_TIMEOUT, MILLISECONDS);
        sHttpClient.readTimeout(MAX_READ_TIMEOUT, MILLISECONDS);

        Retrofit retrofit = sBuilder
                .client(sHttpClient.build())
                .build();
        return retrofit.create(serviceClass);
    }
}
