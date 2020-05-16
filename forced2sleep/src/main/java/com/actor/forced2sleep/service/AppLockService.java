package com.actor.forced2sleep.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.activity.EnterPwdActivity;
import com.actor.forced2sleep.application.MyApplication;
import com.actor.forced2sleep.db.AppLockDao;
import com.actor.forced2sleep.global.Global;
import com.actor.forced2sleep.utils.AccessibilityUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.ActivityUtils;

import java.util.List;

/**
 * Description: 程序锁服务, 辅助功能
 * Company    : 公司名称
 * Date       : 2017/2/23 on 21:31.
 */

public class AppLockService extends AccessibilityService {

    private AppLockDao mDao;
    private String strForce2Sleep;
    private String name;
    protected String channelId = toString();
    protected int id = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        mDao = AppLockDao.getInstance(this);
        strForce2Sleep = getResources().getString(R.string.accessibility_service_label);

        //适配8.0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            name = getResources().getString(R.string.app_name);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(getApplicationContext(), channelId).build();
            startForeground(id, notification);
        }
        AccessibilityUtils.onCreate(this);
    }

    /**
     * 连接服务后,一般是在授权成功后会接收到
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

    /**
     * 辅助功能相关事件发生后的回调,如触发了通知栏变化、界面变化等
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 此方法是在主线程中回调过来的，所以消息是阻塞执行的
        String packageName = event.getPackageName().toString();//当前页面包名
        String className = event.getClassName().toString();
        LogUtils.formatError("包名: %s, className: %s", true, packageName, className);

        List<AccessibilityNodeInfo> nodeInfos = AccessibilityUtils.findNodesByText(event, strForce2Sleep);
        //页面窗口状态发生变化
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                LogUtils.error("窗口状态发生变化: ", true);
                //如果是i管家
                if ("com.iqoo.secure".equals(packageName)) {
                    jumpToEnterPwdActivity(packageName);
                    break;
                }
                //华为桌面的 收纳 文件夹
                if ("com.huawei.android.launcher".equals(packageName) || "android".equals(packageName)) {
                    break;
                }

                //如果是晚上,如果没有在白名单内,已经加锁
                if (Global.isSleepTime() && !mDao.find(packageName)) {
                    if (MyApplication.instance.aCache.getString(packageName) == null) {
                        jumpToEnterPwdActivity(packageName);
                    }
                }
                if (nodeInfos != null && !nodeInfos.isEmpty()) {
                    LogUtils.formatError("在这个页面发现\"%s\": %s", true, strForce2Sleep, nodeInfos.get(0).getClassName().toString());
                    List<AccessibilityNodeInfo> sets = AccessibilityUtils.findNodesByText(event, "设置");
                    List<AccessibilityNodeInfo> unins = AccessibilityUtils.findNodesByText(event, "卸载");
                    if ((sets != null && sets.size() > 0) || (unins != null && unins.size() > 0)) {
                        boolean b = AccessibilityUtils.clickBackKey();
                        LogUtils.formatError("模拟返回键: success = %b", true, b);
                        if (b) {
                            jumpToEnterPwdActivity(packageName);
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED://设置界面的点击事件
                LogUtils.error("界面的点击事件", true);
                switch (packageName) {
                    case "com.android.settings"://设置界面
                        if (nodeInfos != null && !nodeInfos.isEmpty()) {
                            boolean b = AccessibilityUtils.clickBackKey();//取消掉ApertDialog
                            LogUtils.error(String.valueOf(b), true);
                            if (b) {
                                jumpToEnterPwdActivity(packageName);
                            }
                        }
                        break;
                    case "com.iqoo.secure"://i管家
                        jumpToEnterPwdActivity(packageName);
                        break;
                    case "com.huawei.android.launcher"://华为桌面的 收纳 文件夹
                    case "android":
                        break;
                    default:
                        //如果是晚上,如果没有在白名单内,已经加锁
                        if (Global.isSleepTime() && !mDao.find(packageName)) {
                            if (MyApplication.instance.aCache.getString(packageName) == null) {
                                jumpToEnterPwdActivity(packageName);
                            }
                        }
                        break;
                }
                break;
            default:
                /**
                 * @see AccessibilityEvent.EventType
                 */
                LogUtils.formatError("其余事件, EventType = %d: ", true, event.getEventType());
                break;
        }
    }

    private void jumpToEnterPwdActivity(String packageName) {
        //edited 暂时不跳转
        if (true) {
            return;
        }
        ActivityUtils.startActivity(new Intent(this, EnterPwdActivity.class)
                .putExtra(Global.PACKAGE_NAME, packageName));
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
        AccessibilityUtils.onDestroy();
        super.onDestroy();
    }
}
