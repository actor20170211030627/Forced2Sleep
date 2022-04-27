package com.actor.forced2sleep.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import androidx.core.app.NotificationCompat;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.activity.HostManageActivity;
import com.actor.forced2sleep.utils.VpnUtils;
import com.actor.myandroidframework.utils.LogUtils;
import com.blankj.utilcode.util.NotificationUtils;
import com.blankj.utilcode.util.Utils;

import java.io.IOException;

/**
 * description: vpn代理
 * company    :
 * @author : ldf
 * date       : 2022/2/25 on 12
 * @version 1.0
 */
public class OpenVPNService extends VpnService {

    public  static final String               EXTRA_COMMAND = "Command";
    private              ParcelFileDescriptor vpn;
    public static final  int                  START_SERVICE         = 1;
    public static final int RELOAD = 2;
    public static final int STOP = 3;

    @Override
    public void onCreate() {
        LogUtils.error("创建");

        Builder builder = new Builder();
        VpnUtils.initBuilder(builder);
        vpn = VpnUtils.getVpn(builder);
        //开启线程从 vpnInterface.getFileDescriptor 中读取数据包，进行处理
//        FileChannel vpnInput = new FileInputStream(vpnFileDescriptor).getChannel();
//        FileChannel vpnOutput = new FileOutputStream(vpnFileDescriptor).getChannel();
//        vpnInput.read(bufferToNetwork);
//        vpnOutput.write(bufferFromNetwork);

        //将应用的隧道套接字保留在系统 VPN 外部，并避免发生循环连接。
//        protect(int socket | Socket socket | DatagramSocket socket);
//        protect();
        //以将您应用的隧道套接字连接到 VPN 网关。
//        DatagramSocket.connect();
        //在设备上为 VPN 流量配置新的本地 TUN 接口。

        // Listen for connectivity updates
        IntentFilter ifConnectivity = new IntentFilter();
        ifConnectivity.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.formatError("开始接收命令, intent = %s, flags = %d, startId = %d", intent, flags, startId);

        //获取命令
        int cmd = intent.getIntExtra(EXTRA_COMMAND, STOP);
        LogUtils.error("执行：" + cmd);

        switch (cmd) {
            case START_SERVICE:
                /**
                 * https://developer.android.google.cn/guide/components/services#Foreground
                 * 在前台运行服务
                 */
                Intent notificationIntent = new Intent(this, HostManageActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                Notification notification = NotificationUtils.getNotification(NotificationUtils.ChannelConfig.DEFAULT_CHANNEL_CONFIG, new Utils.Consumer<NotificationCompat.Builder>() {
                    @Override
                    public void accept(NotificationCompat.Builder builder) {
                        builder.setContentTitle("ContentTitle")
                                .setContentText("ContentText")
                                .setSmallIcon(R.drawable.logo)
                                .setContentIntent(pendingIntent)
                                .setTicker("Ticker");
                    }
                });
//        Notification notification =
//                new Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
//                        .setContentTitle(getText(R.string.notification_title))
//                        .setContentText(getText(R.string.notification_message))
//                        .setSmallIcon(R.drawable.icon)
//                        .setContentIntent(pendingIntent)
//                        .setTicker(getText(R.string.ticker_text))
//                        .build();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForeground(START_SERVICE, notification);
//            startForegroundService(intent);
                }
                //从前台移除服务
//        stopForeground(true);
                break;
            case STOP:
                stopSelf();
                break;
            default:
                break;
        }


        //START_STICKY_COMPATIBILITY,
        //START_STICKY,
        //START_NOT_STICKY,
        //START_REDELIVER_INTENT,
        return START_REDELIVER_INTENT;
    }

    //停止服务, 可能不会在主线程上调用
    @Override
    public void onRevoke() {
        super.onRevoke();
        LogUtils.error("停止服务");
        //向 VPN 网关关闭受保护隧道套接字。
//        DatagramSocket.close();
        //关闭 parcel 文件描述符（无需清空它）。
//        ParcelFileDescriptor.close();
        if (vpn != null) {
            try {
                vpn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
