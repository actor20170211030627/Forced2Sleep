package com.actor.forced2sleep.utils;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import com.blankj.utilcode.util.AppUtils;

/**
 * description: Vpn工具类
 * https://www.jianshu.com/p/d2e3ccd6bcb3
 * https://www.jianshu.com/p/44a244ad0029
 *
 * company    :
 *
 * @author : ldf
 * date       : 2022/2/25 on 13
 * @version 1.0
 */
public class VpnUtils {

    /**
     * VPNService的创建需要通过Builder去创建及配置，最终生成一个ParcelFileDescriptor对象，
     * 并可以在Builder中进行代理配置
     * @param builder 在VPNService 的子类中new Builder()
     * @return
     */
    public static void initBuilder(VpnService.Builder builder) {
//        if (builder == null) {
        //不能直接new, ∵不是静态类, new不起, 报错
//            builder = new OpenVPNService.Builder();
//        }

//        PackageManager packageManager = context.getPackageManager();

        //设置该次服务名称，服务启动后可在手机设置界面查看
        builder.setSession(AppUtils.getAppName())
                //设置虚拟主机地址和端口, address, port
//                .addAddress("14.215.177.39", 80)//www.baidu.com, 只能是数字, 但是报错
                .addAddress("10.1.10.1", 32)
                .addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 128)
//                .addAddress("2001:db8::1", 64)
                //设置允许通过的路由, address, port
                .addRoute("0.0.0.0", 0)
                .addRoute("0:0:0:0:0:0:0:0", 0)
//                .addRoute("::", 0)

//                .setMtu(int mut)//设置读写操作时最大缓存
//                .addDnsServer("192.168.1.1")//添加域名服务器

                /**
                 * 允许的应用
                 * 如果列表中包含一个或多个应用，则只有列表中的应用才会使用 VPN。
                 * （不在列表中的）所有其他应用都将使用系统网络，就像 VPN 未运行一样。
                 * 当允许列表为空时，所有应用都将使用 VPN。
                 */
//                .addAllowedApplication(String packageName)//添加允许访问连接的程序

                /**
                 * 禁止的应用使用系统网络，就像 VPN 未运行一样，而所有其他应用都将使用 VPN。
                 */
//                .addDisallowedApplication(String packageName)

                /**
                 * 绕过 VPN
                 * 让应用绕过 VPN 并选择自己的网络
                 */
//                .allowBypass()

//                .setConfigureIntent(PendingIntent intent)//设置配置启动项
                ;


//        String[] appPackages = {
//                "com.example.plugindemo1"
//        };

//        for (String appPackage: appPackages) {
//            try {
//                packageManager.getPackageInfo(appPackage, 0);
//                builder.addAllowedApplication(appPackage);
//            } catch (PackageManager.NameNotFoundException e) {
//                // The app isn't installed.
//                LogUtils.error("添加允许的VPN应用出错，未找到");
//                e.printStackTrace();
//            }
//        }
    }

    public static ParcelFileDescriptor getVpn(VpnService.Builder builder) {
        return builder.establish();
    }
}
