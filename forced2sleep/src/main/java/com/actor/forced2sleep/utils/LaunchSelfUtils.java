package com.actor.forced2sleep.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.ToastUtils;

/**
 * Description: 跳转手机"开机自启页面"工具类, https://www.jianshu.com/p/9d729147cea9
 * Author     : 李大发
 * Date       : 2019/12/19 on 17:18
 *
 * @version 1.0
 */
public class LaunchSelfUtils {

    /**
     * 跳转手机"开机自启页面"
     */
    public static boolean gotoLaunchList(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName componentName = null;

        if (RomUtils.isXiaomi()) {// 红米Note4测试通过
            componentName = new ComponentName("com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity");

        } else if (RomUtils.isLeeco()) {// 乐视2测试通过
            intent.setAction("com.letv.android.permissionautoboot");

        } else if (RomUtils.isSamsung()) {// 三星Note5测试通过
            componentName = new ComponentName("com.samsung.android.sm_cn",
                    "com.samsung.android.sm.ui.ram.AutoRunActivity");

        } else if (RomUtils.isHuawei()) {// 华为测试通过
            //我的华为 HONOR v30, 打不开具体页面, 只能打开管家..
            ActivityUtils.startLauncherActivity("com.huawei.systemmanager");
            componentName = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity");

//            componentName = new ComponentName("com.huawei.systemmanager",
//                    "com.huawei.systemmanager.optimize.process.ProtectActivity");

        } else if (RomUtils.isVivo()) {// VIVO测试通过
            //"自启动管理"页面, 打开被拒绝
            componentName = ComponentName.unflattenFromString("com.iqoo.secure/" +
                    ".ui.phoneoptimize.BgStartUpManager");
            //"权限管理"页面, 打开被拒绝
//            componentName = ComponentName.unflattenFromString("com.iqoo.secure/" +
//            ".safeguard.PurviewTabActivity");
            //"软件管理"界面, 打开被拒绝
//            componentName = ComponentName.unflattenFromString("com.iqoo.secure//" +
//            ".appmanager.AppManagerActivity");

        } else if (RomUtils.isMeizu()) {//万恶的魅族
            // 通过测试，发现魅族是真恶心，也是够了，之前版本还能查看到关于设置自启动这一界面，系统更新之后，完全找不到了，心里默默Fuck！
            // 针对魅族，我们只能通过魅族内置手机管家去设置自启动，所以我在这里直接跳转到魅族内置手机管家界面，具体结果请看图
            componentName = ComponentName.unflattenFromString("com.meizu.safe/" +
                    ".permission.PermissionMainActivity");

        } else if (RomUtils.isOppo()) {// OPPO R8205测试通过
            componentName = ComponentName.unflattenFromString("com.oppo.safe/" +
                    ".permission.startup.StartupAppListActivity");

        } else if (RomUtils.is360()) {// 360手机 未测试
            componentName = new ComponentName("com.yulong.android.coolsafe",
                    ".ui.activity.autorun.AutoRunListActivity");
        } else {
            // 以上只是市面上主流机型，由于公司你懂的，所以很不容易才凑齐以上设备
            // 针对于其他设备，我们只能调整当前系统app查看详情界面
            // 在此根据用户手机当前版本跳转系统设置界面
            if (Build.VERSION.SDK_INT >= 9) {
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName("com.android.settings",
                        "com.android.settings.InstalledAppDetails");
                intent.putExtra("com.android.settings.ApplicationPkgName",
                        context.getPackageName());
            }
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
            if (e instanceof ActivityNotFoundException) {
                ToastUtils.showShort("打开的页面不对...");
            } else if (e instanceof SecurityException) {
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
}
