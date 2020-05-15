package com.actor.forced2sleep.application;

import android.support.annotation.NonNull;

import com.actor.myandroidframework.application.ActorApplication;
import com.zhouyou.http.EasyHttp;
import com.zhy.http.okhttp.OkHttpUtils;

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

    @Override
    protected void configEasyHttp(EasyHttp easyHttp) {
        easyHttp.setConnectTimeout(60_000L)
                .setReadTimeOut(60_000L)
                .setWriteTimeOut(60_000L);
        //配置张鸿洋的OkHttpUtils
        OkHttpUtils.initClient(EasyHttp.getOkHttpClient());
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
