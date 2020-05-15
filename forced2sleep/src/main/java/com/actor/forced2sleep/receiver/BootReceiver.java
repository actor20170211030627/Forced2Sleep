package com.actor.forced2sleep.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.actor.forced2sleep.service.AppLockService;
import com.actor.forced2sleep.service.ToastNoticeService;
import com.actor.forced2sleep.utils.AccessibilityUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.blankj.utilcode.util.ToastUtils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {//"android.intent.action.BOOT_COMPLETED"
            if (!ServiceUtils.isServiceRunning(AppLockService.class)) {
                //跳到系统辅助功能页面
                AccessibilityUtils.openAccessibility(context);
                ToastUtils.showShort("请开启辅助功能");
            } else {
                ToastUtils.showShort("辅助功能已开启");
            }
        }
        context.startService(new Intent(context, ToastNoticeService.class));
    }


}
