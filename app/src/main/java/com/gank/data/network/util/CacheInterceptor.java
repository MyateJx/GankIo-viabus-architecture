package com.gank.data.network.util;


import com.gank.GankApp;
import com.gank.util.NetWorkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        if (!NetWorkUtils.isNetWorkAvailable(GankApp.getAppContext())) {

            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response response = chain.proceed(request);
        // 10秒钟之内连续请求多次使用缓存数据
        if (NetWorkUtils.isNetWorkAvailable(GankApp.getAppContext())) {
            int maxAge = 10;
            response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .removeHeader("Pragma")
                    .build();

        }
        return response;
    }
}
