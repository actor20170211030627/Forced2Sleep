package com.actor.forced2sleep.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.actor.forced2sleep.service.AppLockService;
import com.actor.forced2sleep.utils.ServiceStateUtils;
import com.actor.myandroidframework.utils.ToastUtils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {//"android.intent.action.BOOT_COMPLETED"
            if (!ServiceStateUtils.isServiceRunning(context, AppLockService.class)) {
                //跳到系统辅助功能页面
                //隐式意图
                context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                ToastUtils.show("请开启辅助功能");
            } else {
                ToastUtils.show("辅助功能已开启");
            }
		}
    }


}
