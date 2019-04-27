package com.actor.forced2sleep.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.activity.EnterPwdActivity;
import com.actor.forced2sleep.activity.MainActivity;
import com.actor.forced2sleep.application.MyApplication;
import com.actor.forced2sleep.db.AppLockDao;
import com.actor.forced2sleep.utils.AccessibilityUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Description: 程序锁服务
 * Copyright  : Copyright (c) 2015
 * Company    : 公司名称
 * Date       : 2017/2/23 on 21:31.
 */

public class AppLockService extends AccessibilityService {

    private AppLockDao mDao;

    @Override
    public void onCreate() {
        super.onCreate();
        mDao = AppLockDao.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("AppLockService:onStartCommand");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("强制睡觉正在运行中...");
        builder.setSmallIcon(R.drawable.pangu);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pangu));
        builder.setContentTitle("强制睡觉");
        builder.setContentText("正在运行中...");
        //builder.setContentInfo("Content Info");
        builder.setWhen(System.currentTimeMillis());
        Intent intent1 = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent
                .FLAG_NO_CREATE);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        //在onStartCommand里面调用 startForeground
        startForeground(9529, notification);//id 唯一的通知标识
        return Service.START_STICKY;//super.onStartCommand(intent, flags, startId)
    }

    /**
     * //连接服务后,一般是在授权成功后会接收到
     * 系统会在成功连接上你的服务的时候调用这个方法，在这个方法里你可以做一下初始化工作，
     * 例如设备的声音震动管理，也可以调用setServiceInfo()进行配置工作。
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // 通过代码可以动态配置，但是可配置项少一点
//        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
//        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED
//                | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
//                | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
//                | AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
//        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
//        accessibilityServiceInfo.notificationTimeout = 0;
//        accessibilityServiceInfo.flags = AccessibilityServiceInfo.DEFAULT;
//        setServiceInfo(accessibilityServiceInfo);
    }

    //辅助功能相关事件发生后的回调,如触发了通知栏变化、界面变化等
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 此方法是在主线程中回调过来的，所以消息是阻塞执行的
        String packageName = event.getPackageName().toString();//当前页面包名
        System.out.println(packageName);

        // AccessibilityOperator封装了辅助功能的界面查找与模拟点击事件等操作
        AccessibilityUtils.updateEvent(this, event);
        List<AccessibilityNodeInfo> nodeInfos = AccessibilityUtils.findNodesByText(getResources()
                .getString(R.string.accessibility_service_label));
        //页面窗口状态发生变化
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                System.out.println("窗口状态发生变化");
                //如果是i管家
                if ("com.iqoo.secure".equals(packageName)) {
                    jumpToEnterPwdActivity(packageName);
                    break;
                }
                //如果是晚上,如果没有在白名单内,已经加锁
                if (isNight() && !mDao.find(packageName)) {
                    if (MyApplication.instance.aCache.getAsString(packageName) == null) {
                        jumpToEnterPwdActivity(packageName);
                    }
                }
                if (nodeInfos != null && nodeInfos.size() > 0) {
                    System.out.println(nodeInfos.get(0).getClassName());
                    List<AccessibilityNodeInfo> sets = AccessibilityUtils.findNodesByText("设置");
                    List<AccessibilityNodeInfo> unins = AccessibilityUtils.findNodesByText("卸载");
                    if ((sets != null && sets.size() > 0) || (unins != null && unins.size() > 0)) {
                        boolean b = AccessibilityUtils.clickBackKey();
                        System.out.println("模拟返回键:" + String.valueOf(b));
                        if (b) {
                            jumpToEnterPwdActivity(packageName);
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED://设置界面的点击事件
                System.out.println("界面的点击事件");
                switch (packageName) {
                    case "com.android.settings"://设置界面
                        if (nodeInfos != null && !nodeInfos.isEmpty()) {
                            boolean b = AccessibilityUtils.clickBackKey();//取消掉ApertDialog
                            System.out.println(b);
                            if (b) {
                                jumpToEnterPwdActivity(packageName);
                            }
                        }
                        break;
                    case "com.iqoo.secure"://i管家
                        jumpToEnterPwdActivity(packageName);
                        break;
                    default:
                        //如果是晚上,如果没有在白名单内,已经加锁
                        if (isNight() && !mDao.find(packageName)) {
                            if (MyApplication.instance.aCache.getAsString(packageName) == null) {
                                jumpToEnterPwdActivity(packageName);
                            }
                        }
                        break;
                }
                break;
        }
    }

    //是否是夜晚
    private boolean isNight(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour < 8 || hour >= 22;
    }

    private void jumpToEnterPwdActivity(String packageName){
        Intent intent = new Intent(this, EnterPwdActivity.class);
        intent.putExtra("package", packageName);

        //如果从service中启动activity, 需要加标记,FLAG_ACTIVITY_NEW_TASK, 表示新建一个任务栈
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //异常:Calling startActivity() from outside of an Activity  context requires the
        // FLAG_ACTIVITY_NEW_TASK
        startActivity(intent);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        //接收按键事件
        return super.onKeyEvent(event);
    }

    @Override
    public void onInterrupt() {
        //辅助功能服务中断，如授权关闭或者将服务杀死
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, AppLockService.class));
    }
}
