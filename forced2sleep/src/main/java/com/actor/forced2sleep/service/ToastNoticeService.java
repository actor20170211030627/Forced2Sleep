package com.actor.forced2sleep.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.db.AppLockDao;
import com.actor.forced2sleep.global.Global;
import com.actor.myandroidframework.service.BaseService;
import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.PermissionRequestUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

/**
 * Description: toast提醒服务
 * Author     : 李大发
 * Date       : 2019/11/29 on 11:39
 */
public class ToastNoticeService extends BaseService {

    private AppLockDao               mDao;
    public  String                   toastContent;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean sleepTime = Global.isSleepTime();
            if (sleepTime) {
                boolean screenLock = ScreenUtils.isScreenLock();//是否锁屏
                if (!screenLock) {
                    String packageName = getProcessName();
                    if (!mDao.find(packageName)) {
                        ToastUtils.showShort(toastContent);
                    }
                }
            }
            sendEmptyMessageDelayed(0, 1_000L);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mDao = AppLockDao.getInstance(this);
        toastContent = getResources().getString(R.string.toast_sleep);
        handler.sendEmptyMessage(0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            checkPermission();
        }
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
        //我的华为手机一直返回 false, 原生代码也是一直返回 -1
        String permission = Manifest.permission.PACKAGE_USAGE_STATS;
        PermissionRequestUtils.requestPermission(this, new PermissionRequestUtils.PermissionCallBack() {
            @Override
            public void onGranted(@NonNull List<String> deniedPermissions) {
                LogUtils.error("onGranted: 同意权限", true);
            }

            @Override
            public void onDenied(@NonNull List<String> deniedPermissions) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, permission);
    }

    //<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"
    //        tools:ignore="ProtectedPermissions" />
    private String getProcessName() {
        //华为手机 "HONOR V30" 获取不准确, 暂未找到解决方法...
        return ProcessUtils.getForegroundProcessName();
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            long now = System.currentTimeMillis();
//            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
//            //获取60秒之内的应用数据
//            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);
//            if (queryUsageStats == null || queryUsageStats.isEmpty()) {// 没有权限，获取不到数据
//                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            } else {
//                UsageStats usageStats = null;
//                for (int i = 0; i < queryUsageStats.size(); i++) {//在list中找出最后使用的app
//                    UsageStats usageStats1 = queryUsageStats.get(i);
//                    if (usageStats == null || usageStats.getLastTimeUsed() < usageStats1.getLastTimeUsed()) {
//                        usageStats = usageStats1;
//                    }
//                }
//                return usageStats.getPackageName();
//            }
//        } else {
//            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//            return manager.getRunningTasks(1).get(0).topActivity.getPackageName();
//        }
//        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
