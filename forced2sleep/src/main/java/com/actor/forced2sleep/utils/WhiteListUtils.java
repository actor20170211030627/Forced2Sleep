package com.actor.forced2sleep.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.actor.myandroidframework.utils.LogUtils;
import com.actor.myandroidframework.utils.ToastUtils;
import com.blankj.utilcode.util.RomUtils;

/**
 * Description: 跳转手机"白名单"页面工具类
 * Author     : 李大发
 * Date       : 2019/12/19 on 18:25
 *
 * @version 1.0
 */
public class WhiteListUtils {

    public static boolean gotoWhiteList(Context context) {
        String phoneType = RomUtils.getRomInfo().getName();
        LogUtils.error("当前手机型号为：" + phoneType, true);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ComponentName componentName = null;

        if (RomUtils.isXiaomi()) {//小米
//            componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter" +
//                    ".autostart.AutoStartManagementActivity");

        } else if (RomUtils.isLeeco()) {//乐视
//            intent.setAction("com.letv.android.permissionautoboot");

        } else if (RomUtils.isSamsung()) {//三星
//            componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung" +
//                    ".android.sm.ui.ram.AutoRunActivity");

        } else if (RomUtils.isHuawei()) {//华为
//            componentName = new ComponentName("com.huawei.systemmanager", "com.huawei" +
//                    ".systemmanager.optimize.process.ProtectActivity");

        } else if (RomUtils.isVivo()) {//VIVO
            componentName = ComponentName.unflattenFromString("com.iqoo.secure/.ui.phoneoptimize.AddWhiteListActivity");

        } else if (RomUtils.isMeizu()) {//魅族
//            componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission" +
//                    ".PermissionMainActivity");

        } else if (RomUtils.isOppo()) {//OPPO
//            componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission" +
//                    ".startup.StartupAppListActivity");

        } else if (RomUtils.is360()) {//360手机
//            componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity" +
//                    ".autorun.AutoRunListActivity");

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
                ToastUtils.show("打开自启页面被系统拒绝了");
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
