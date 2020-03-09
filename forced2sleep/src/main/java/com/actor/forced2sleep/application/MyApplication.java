package com.actor.forced2sleep.application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.actor.forced2sleep.activity.SplashActivity;
import com.actor.forced2sleep.utils.ACache;
import com.actor.myandroidframework.application.ActorApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by zhengping on 2017/4/2,10:03.
 *
 * 1、生命周期长
 *      存一些东西（退出应用程序按钮）
 *      内存泄露：生命周期短的被生命周期长的对象长期引用
 * 2、单例（一个进程只有一个Application的实例对象）
 * 3、onCreate方法是一个应用程序入口
 *
 *
 * 4、注意：自定义的Application需要在清单文件中注册
 *
 */

public class MyApplication extends ActorApplication {

    public static MyApplication instance;
    public        ACache        aCache;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        aCache = ACache.get(this);
    }

    @Nullable
    @Override
    protected OkHttpClient.Builder getOkHttpClientBuilder(OkHttpClient.Builder builder) {
        return builder.connectTimeout(60_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
                .readTimeout(60_000L, TimeUnit.MILLISECONDS)//默认10s, 可不设置
                .writeTimeout(60_000L, TimeUnit.MILLISECONDS);//默认10s, 可不设置
    }

    @NonNull
    @Override
    protected String getBaseUrl() {
        return "https://api.github.com";
    }

    @Override
    protected void onUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent restartIntent = PendingIntent.getActivity(this, 0, intent, 0);//PendingIntent.FLAG_...
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);//1000:1秒钟后重启应用
        System.exit(-1);//退出
    }
}
