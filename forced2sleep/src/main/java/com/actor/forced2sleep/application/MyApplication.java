package com.actor.forced2sleep.application;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.actor.myandroidframework.application.ActorApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 强制睡觉
 */
public class MyApplication extends ActorApplication {

    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Nullable
    @Override
    protected OkHttpClient.Builder configOkHttpClientBuilder(OkHttpClient.Builder builder) {
        return builder.connectTimeout(60_000L, TimeUnit.MILLISECONDS)
                .readTimeout(60_000L, TimeUnit.MILLISECONDS)
                .writeTimeout(60_000L, TimeUnit.MILLISECONDS);
    }

    @NonNull
    @Override
    protected String getBaseUrl() {
        return "https://api.github.com";
    }

    @Override
    protected void onUncaughtException(Thread thread, Throwable e) {
//        System.exit(-1);//退出
    }
}
