package com.pitaya.baselib.network;

import com.elvishew.xlog.XLog;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {

    private static HashMap<Class, Object> mServiceConfigs = new HashMap<>();

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getApi(Class<T> clazz) {

        Object api = mServiceConfigs.get(clazz);
        if (api != null)
            return (T) api;

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://www.baidu.com")
                    .callFactory(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            api = retrofit.create(clazz);
            mServiceConfigs.put(clazz, api);
            return (T) api;
        } catch (Throwable e) {
            XLog.e("ApiFactory", e);
            return null;
        }
    }
}