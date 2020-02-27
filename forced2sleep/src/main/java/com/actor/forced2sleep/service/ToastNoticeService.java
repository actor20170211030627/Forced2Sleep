package com.actor.forced2sleep.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.db.AppLockDao;
import com.actor.forced2sleep.global.Global;
import com.actor.myandroidframework.utils.PermissionRequestUtils;
import com.actor.myandroidframework.utils.ToastUtils;
import com.blankj.utilcode.util.ScreenUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Description: toast提醒服务
 * Author     : 李大发
 * Date       : 2019/11/29 on 11:39
 */
public class ToastNoticeService extends Service {

    private Timer      timer = new Timer();
    private AppLockDao mDao;
    public String toastContent;

    @Override
    public void onCreate() {
        super.onCreate();
        mDao = AppLockDao.getInstance(this);
        toastContent = getResources().getString(R.string.toast_sleep);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            checkPermission();
        } else initTimer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 此方法判断是否打开了数据量访问的权限
     * @return
     */
    private void checkPermission() {
        PermissionRequestUtils.requestPermission(this, new PermissionRequestUtils.PermissionCallBack() {
            @Override
            public void onSuccessful(@NonNull List<String> deniedPermissions) {
                initTimer();
            }

            @Override
            public void onFailure(@NonNull List<String> deniedPermissions) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, Manifest.permission.PACKAGE_USAGE_STATS);
    }

    private void initTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String packageName = getProcessName();
                if (Global.isSleepTime() && !mDao.find(packageName)) {
                    boolean screenLock = ScreenUtils.isScreenLock();//是否锁屏
                    if (!screenLock) ToastUtils.show(toastContent);
                }
            }
        }, 0, 1000);
    }

    //<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"
    //        tools:ignore="ProtectedPermissions" />
    private String getProcessName() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            long ts = System.currentTimeMillis();
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
            if (queryUsageStats == null || queryUsageStats.isEmpty()) {// 没有权限，获取不到数据
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                UsageStats usageStats = null;
                for (int i = 0; i < queryUsageStats.size(); i++) {//在list中找出最后使用的app
                    UsageStats usageStats1 = queryUsageStats.get(i);
                    if (usageStats == null || usageStats.getLastTimeUsed() < usageStats1.getLastTimeUsed()) {
                        usageStats = usageStats1;
                    }
                }
                return usageStats.getPackageName();
            }
        } else {
//        String foregroundProcessName = ProcessUtils.getForegroundProcessName();//不起作用

            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            return manager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();//取消后再调用timer.schedule()会报错
    }
}
