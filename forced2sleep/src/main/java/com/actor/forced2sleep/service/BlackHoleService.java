package com.actor.forced2sleep.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.actor.forced2sleep.R;

import java.io.IOException;

/**
 * description: 网络防火墙---使用VpnService遇到的坑 - 简书.html
 * https://www.jianshu.com/p/7951b08b020a
 *
 * company    :
 *
 * @author : ldf
 * date       : 2022/3/4 on 11
 * @version 1.0
 */
public class BlackHoleService extends VpnService {


    private static final String TAG = "NetGuard.Service";

    private static final String EXTRA_COMMAND = "Command";

    private ParcelFileDescriptor vpn = null;

    public static final int START = 1;

    public static final int RELOAD = 2;

    public static final int STOP = 3;

    @Override
    public void onCreate() {
        // Listen for connectivity updates
        IntentFilter ifConnectivity = new IntentFilter();
        ifConnectivity.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityChangedReceiver, ifConnectivity);
        super.onCreate();
    }

    private BroadcastReceiver connectivityChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received " + intent);
            if (intent.hasExtra(ConnectivityManager.EXTRA_NETWORK_TYPE)) {
                reload(BlackHoleService.this);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get command
        int cmd = intent.getIntExtra(EXTRA_COMMAND, RELOAD);
        Log.e(TAG, "执行：" + cmd);
        // Process command
        switch (cmd) {
            case START:
//                boolean connected = NetworkUtils.isConnected();
//                if (NetworkUtils.isNetworkAvailable(this) && vpn == null) {
//                    vpnStart();
//                }
                break;
            case RELOAD:
                ParcelFileDescriptor prev = vpn;
                vpnStart();
                if (prev != null) {
                    vpnStop(prev);
                }
                break;
            case STOP:
                if (vpn != null) {
                    vpnStop(vpn);
                    vpn = null;
                }
                stopSelf();
                break;
            default:
                break;
        }
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void vpnStart() {
        Log.e(TAG, "Starting");
        final Builder builder = new Builder();
        builder.setSession(getString(R.string.app_name));
        builder.addAddress("10.1.10.1", 32);
        builder.addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 128);
        builder.addRoute("0.0.0.0", 0);
        builder.addRoute("0:0:0:0:0:0:0:0", 0);
        try {
//            builder.addDisallowedApplication(MainActivity.ALLOW_PACKAGE_NAME);
//            builder.addDisallowedApplication("com.google.android.gms");
//            builder.addDisallowedApplication(getPackageName());
            vpn = builder.establish();
            Log.e(TAG, "启动完成");
        } catch (Exception e) {
            Log.e(TAG, "大爷的，是不是这里有问题？");
            Log.e(TAG, e.toString());
        }
    }

    private void vpnStop(ParcelFileDescriptor prev) {
        if (prev != null) {
            try {
                prev.close();
            } catch (IOException ex) {
                Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
            }
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, BlackHoleService.class);
        intent.putExtra(EXTRA_COMMAND, START);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, BlackHoleService.class);
        intent.putExtra(EXTRA_COMMAND, STOP);
        context.startService(intent);
    }

    public static void reload(Context context) {
//        if (BlockUtils.isLock()) {
//            Intent intent = new Intent(context, BlackHoleService.class);
//            intent.putExtra(EXTRA_COMMAND, RELOAD);
//            context.startService(intent);
//        }
    }

    public static Intent isVpnServicePrepared(Context context) {
        Intent prepare = null;
        try {
            return VpnService.prepare(context.getApplicationContext());
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return prepare;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "VPNService Destroy");
        unregisterReceiver(connectivityChangedReceiver);
        super.onDestroy();
    }
}
