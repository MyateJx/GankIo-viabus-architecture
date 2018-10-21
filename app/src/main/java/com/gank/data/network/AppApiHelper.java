package com.gank.data.network;

import android.content.Context;

import com.gank.Constants;
import com.gank.data.network.response.ThemeResponse;
import com.gank.data.network.util.CacheInterceptor;
import com.gank.util.FileUtils;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gank.Constants.baseUrl;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public class AppApiHelper implements ApiHelper {
    ApiService mApiSerVice;

    public AppApiHelper(Context context) {
        this.mApiSerVice = getRetrofit(context).create(ApiService.class);
    }

    @Override
    public Observable<ThemeResponse> getThemeDataCall(String path) {
        return mApiSerVice.getThemeDataCall(path);
    }

    @Override
    public Observable<ThemeResponse> getSearchDataCall(String content, String type, String page) {
        return mApiSerVice.getSearchDataCall(content, type, page);
    }

    Retrofit getRetrofit(Context context) {
        Gson gson = new Gson();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(new Cache(FileUtils.getHttpCacheDir(context), Constants.Config.HTTP_CACHE_SIZE))
                .connectTimeout(Constants.Config.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(Constants.Config.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new CacheInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }


}
