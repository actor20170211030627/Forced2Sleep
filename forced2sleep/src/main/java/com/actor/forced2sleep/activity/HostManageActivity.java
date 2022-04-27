package com.actor.forced2sleep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.databinding.ActivityHostManageBinding;
import com.actor.forced2sleep.service.OpenVPNService;
import com.blankj.utilcode.util.FileIOUtils;

import java.io.File;
import java.util.List;

/**
 * description: 手机Host管理, 资料:
 * 开发文档: https://developer.android.google.cn/guide/topics/connectivity/vpn#java
 * 官方demo clone: https://github.com/iceleaf916/ToyVPN (要输入ip,端口,secret, 然后点击连接, 没看见钥匙形状图标)
 * 一个本地拦截软件: blockada.apk
 *
 * company    :
 *
 * @author : ldf
 * date       : 2022/2/24 on 16:23
 */
public class HostManageActivity extends BaseActivity<ActivityHostManageBinding> {

    /**
     * @see com.blankj.utilcode.util.FileUtils#LINE_SEP
     */
    private static final String              LINE_SEP = System.getProperty("line.separator");
    private              List<String>        lines;
    private              File                file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("手机Host管理");

        //1.修改host文件首先需要Android手机获取Root权限
        file = new File("/system/etc/hosts");
        lines = FileIOUtils.readFile2List(file);

        //修改hosts方法二：
        //将hosts文件拷贝到电脑，电脑端修改后复制回手机(但需要root权限)

        //3.设备管理器

        //4.VPN

    }

    @Override
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_print_ip:
                /**
                 * 准备服务:
                 * https://developer.android.google.cn/guide/topics/connectivity/vpn#prepare_a_service
                 * 要使应用准备好成为用户当前的 VPN 服务，请调用 VpnService.prepare()。
                 * 如果设备的使用者尚未授予应用的权限，则该方法会返回 Activity Intent。您将使用此 Intent 来启动询问权限的系统 Activity。
                 * 系统会显示一个类似于其他权限对话框（例如摄像头或联系人访问权限）的对话框。
                 * 如果您的应用已经准备好，则该方法会返回 null。
                 */
                @Nullable
                Intent intent = OpenVPNService.prepare(this);
                if (intent != null) {
//                    ContextCompat.startForegroundService(this, intent);
                    startActivityForResult(intent, 0);
                } else {
                    onActivityResult(0, RESULT_OK, null);
                }
//                printIp();
                break;
            case R.id.btn_add_ip:
//                addIp();
                //关闭vpn连接
                Intent intentStop = new Intent(this, OpenVPNService.class);
                intentStop.putExtra(OpenVPNService.EXTRA_COMMAND, OpenVPNService.STOP);
                startService(intentStop);
                //下面这种方式停止不了
//                stopService(intentStop);
                break;
            case R.id.btn_is_start:
//                boolean isConnected = isVpnUsed();
//                String ip = IpUtil.getIPAddress(this);
//                toastFormat("vpn连接状态: %b, 连接ip: %s", isConnected, ip);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intentStart = new Intent(this, OpenVPNService.class);
            intentStart.putExtra(OpenVPNService.EXTRA_COMMAND, OpenVPNService.START_SERVICE);
            startService(intentStart);
        }
    }

    private void printIp() {
        for (String line : lines) {
            //         127.0.0.1       localhost
            //         ::1             ip6-localhost
            logError(line);
        }
    }

    private void addIp() {
        //         127.0.0.1       localhost
        //         ::1             ip6-localhost
        //起点
        lines.add("127.0.0.1       read.qidian.com");
        lines.add("127.0.0.1       www.qidian.com");
        //# 搜读
        lines.add("127.0.0.1       m.sodu2020.com");
        //# 头条
        lines.add("127.0.0.1       www.toutiao.com");
        lines.add("127.0.0.1       m.toutiao.com");
        //# 西瓜
        lines.add("127.0.0.1       www.ixigua.com");
        lines.add("127.0.0.1       m.ixigua.com");
        FileIOUtils.writeFileFromString(file, list2String());
    }

    private String list2String() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            sb.append(lines.get(i));
            //如果不是最后一个
            if (i < lines.size() - 1) {
                sb.append(LINE_SEP);
            }
        }
        return sb.toString();
    }
}