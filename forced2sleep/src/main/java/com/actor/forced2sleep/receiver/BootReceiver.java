package com.actor.forced2sleep.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.actor.forced2sleep.service.ToastNoticeService;
import com.blankj.utilcode.util.ToastUtils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            ToastUtils.showShort("收到开机广播!!!");
        }
        context.startService(new Intent(context, ToastNoticeService.class));
    }
}
