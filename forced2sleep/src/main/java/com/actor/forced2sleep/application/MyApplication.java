package com.actor.forced2sleep.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.actor.forced2sleep.global.Global;
import com.actor.myandroidframework.application.ActorApplication;
import com.actor.myandroidframework.utils.database.GreenDaoUtils;
import com.greendao.gen.NovelBeanDao;

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
        GreenDaoUtils.init(this, isAppDebug(), "ban_url.db3", null, NovelBeanDao.class);
    }

    @Nullable
    @Override
    protected OkHttpClient.Builder configOkHttpClientBuilder(OkHttpClient.Builder builder) {
        return builder;
    }

    @NonNull
    @Override
    protected String getBaseUrl(boolean isDebugMode) {
        return Global.BASE_URL;
    }

    @Override
    protected void onUncaughtException(Throwable e) {
//        System.exit(-1);//退出
    }
}
