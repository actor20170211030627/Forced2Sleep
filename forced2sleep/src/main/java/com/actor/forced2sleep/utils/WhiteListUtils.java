package com.actor.forced2sleep.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.ToastUtils;

/**
 * Description: 跳转手机"白名单"页面工具类
 * Author     : 李大发
 * Date       : 2019/12/19 on 18:25
 *
 * @version 1.0
 */
public class WhiteListUtils {

    /**
     * 是否忽略电池优化
     * <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
     */
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        boolean isIgnoring = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Android 6.0
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
                return isIgnoring;
            }
        }
        return isIgnoring;
    }

    /**
     * 忽略电池优化
     */
    public static void requestIgnoreBatteryOptimizations(Activity activity, int requestCode) {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转手机白名单
     * https://www.jianshu.com/p/32b7241124a2?utm_campaign=haruki
     */
    public static boolean gotoWhiteList(Context context) {
        String phoneType = RomUtils.getRomInfo().getName();
        LogUtils.error("当前手机型号为：" + phoneType, true);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ComponentName componentName = null;

        if (RomUtils.isXiaomi()) {//小米: 操作步骤：授权管理 -> 自启动管理 -> 允许应用自启动
            showActivity(context, "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity");
        } else if (RomUtils.isLeeco()) {//乐视 操作步骤：自启动管理 -> 允许应用自启动
//            intent.setAction("com.letv.android.permissionautoboot");
            showActivity(context, "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity");
        } else if (RomUtils.isSamsung()) {//三星 操作步骤：自动运行应用程序 -> 打开应用开关 -> 电池管理 -> 未监视的应用程序 -> 添加应用
//            componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung" +
//                    ".android.sm.ui.ram.AutoRunActivity");
            try {
                showActivity(context, "com.samsung.android.sm_cn");
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    showActivity(context, "com.samsung.android.sm");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else if (RomUtils.isHuawei()) {//华为: 操作步骤：应用启动管理 -> 关闭应用开关 -> 打开允许自启动
//            componentName = new ComponentName("com.huawei.systemmanager",
//                    "com.huawei.systemmanager.optimize.process.ProtectActivity");
            try {
                showActivity(context, "com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    showActivity(context, "com.huawei.systemmanager",
                            "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else if (RomUtils.isVivo()) {//VIVO 操作步骤：权限管理 -> 自启动 -> 允许应用自启动
            componentName = ComponentName.unflattenFromString("com.iqoo.secure/.ui.phoneoptimize.AddWhiteListActivity");
//            showActivity(context, "com.iqoo.secure");
        } else if (RomUtils.isMeizu()) {//魅族 操作步骤：权限管理 -> 后台管理 -> 点击应用 -> 允许后台运行
//            componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission" +
//                    ".PermissionMainActivity");
            showActivity(context, "com.meizu.safe");
        } else if (RomUtils.isOppo()) {//OPPO: 操作步骤：权限隐私 -> 自启动管理 -> 允许应用自启动
//            componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission" +
//                    ".startup.StartupAppListActivity");
            try {
                showActivity(context, "com.coloros.phonemanager");
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    showActivity(context, "com.oppo.safe");
                } catch (Exception e1) {
                    e1.printStackTrace();
                    try {
                        showActivity(context, "com.coloros.oppoguardelf");
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        try {
                            showActivity(context, "com.coloros.safecenter");
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                }
            }
        } else if (RomUtils.is360()) {//360手机
//            componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity" +
//                    ".autorun.AutoRunListActivity");

        } else if (RomUtils.isSmartisan()) {//锤子 操作步骤：权限管理 -> 自启动权限管理 -> 点击应用 -> 允许被系统启动
            showActivity(context, "com.smartisanos.security");

        } else {//其他设备，我们只能调整当前系统app查看详情界面
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }

        intent.setComponent(componentName);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {//抛出异常就直接打开设置页面
            e.printStackTrace();
            //java.lang.SecurityException: Permission Denial: starting Intent
            // { flg=0x10000000 cmp=com.iqoo.secure/.safeguard.PurviewTabActivity }
            // from ProcessRecord{32d63763 25125:com.actor.forced2sleep/u0a1772}
            // (pid=25125, uid=11772) not exported from uid 10053
            if (e instanceof SecurityException) {
                ToastUtils.showShort("打开自启页面被系统拒绝了");
            }
//            gotoSetting(context);
        }
        return false;
    }

    public static void gotoSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 跳转到指定应用的首页
     */
    private static void showActivity(Context context, @NonNull String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private static void showActivity(Context context, @NonNull String packageName, @NonNull String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
