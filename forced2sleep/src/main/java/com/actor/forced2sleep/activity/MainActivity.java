package com.actor.forced2sleep.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.databinding.ActivityMainBinding;
import com.actor.forced2sleep.db.AppLockDao;
import com.actor.forced2sleep.service.ToastNoticeService;
import com.actor.forced2sleep.utils.CheckUpdateUtils;
import com.actor.forced2sleep.utils.LaunchSelfUtils;
import com.actor.forced2sleep.utils.WhiteListUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.google.android.material.snackbar.Snackbar;

import butterknife.OnClick;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private Button btn;
    private int redTransCC99;
    private       Snackbar   snackbar;

    private final AppLockDao appLockDao = AppLockDao.getInstance(this);
    private static final int        REQUEST_BATTERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StatusBarUtil.setTransparentForImageView(this, null);
        BarUtils.transparentStatusBar(this);
        btn = viewBinding.btn;
        redTransCC99 = getResources().getColor(R.color.red_trans_CC99);

        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                toast(data.getAuthority());
                logError(data.getAuthority());//传递的内容
            } else {
                logError("data == null, from SplashActivity");
            }
        }
        snackbar = Snackbar.make(btn, "color:#cc99", Snackbar.LENGTH_INDEFINITE);

        /**
         * 下面这些是白名单
         */
        /**
         * 添加vivo手机
         */
        addPackageName("android");//系统启动/关机界面
        addPackageName("com.android.BBKClock");//闹钟
        addPackageName("com.android.camera");//拍照
        addPackageName("com.android.contacts");//添加联系人
        addPackageName("com.android.dialer");//拨号,打电话
        addPackageName("com.android.incallui");//拨号中...
        addPackageName("com.android.mms");//信息,短信
        addPackageName("com.android.notes");//便签
        addPackageName("com.android.packageinstaller");//安装app界面
        addPackageName("com.android.server.telecom");//联系人-->个人-->号码分类标记
        addPackageName("com.android.settings");//设置/辅助功能页面
        addPackageName("com.android.stk");//vivo手机弹出的那个超恶心的框框
        addPackageName("com.android.systemui");//锁屏 & 往上滑和往下滑那个
        addPackageName("com.android.wifisettings");//WLAN,wifi设置
        addPackageName("com.alensw.PicFolder");//快图浏览
        addPackageName("com.alibaba.android.rimet");//钉钉
        addPackageName("com.bbk.calendar");//日历
        addPackageName("com.bbk.launcher2");//桌面
        addPackageName("com.bbk.SuperPowerSave");//超级省电界面
//        addPackageName("com.iqoo.powersaving");//超级省电
        //addPackageName("com.iqoo.secure");//通话详情-->加入黑名单&隐私通讯&骚扰拦截&i管家
        addPackageName("com.actor.forced2sleep");//把自己加进去
        addPackageName("com.vivo.browser");//浏览器
        addPackageName("com.vivo.gallery");//相机相册

        /**
         * 添加华为
         */
        addPackageName("com.huawei.intelligent");
        addPackageName("com.huawei.android.launcher");//华为桌面

        /**
         * 添加第三方
         */
        addPackageName("com.autonavi.minimap");//高德地图
        addPackageName("com.eg.android.AlipayGphone");//支付宝
        addPackageName("com.kuchuan.getsign");//酷川云获取签名
        addPackageName("com.MobileTicket");//铁路12306
        addPackageName("com.starrymedia.metro.best");//最地铁
        addPackageName("com.tc.cm");//地铁通
        addPackageName("com.tencent.androidqqmail");//腾讯邮箱
        addPackageName("com.tencent.mm");//微信
        addPackageName("com.tencent.mobileqq");//QQ

        //检查更新
        new CheckUpdateUtils().check(this);
        startForegroundService(new Intent(this, ToastNoticeService.class));
    }

    @Override
    @OnClick({R.id.btn_launch, R.id.btn_battery_ignore, R.id.btn_white, R.id.btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_launch://开机启动
                toast("开机启动(未完成,需自己开启)");
                boolean success = LaunchSelfUtils.gotoLaunchList(this);
                if (!success) {
                    openApk("com.iqoo.secure");//打开i管家
                }
                break;
            case R.id.btn_battery_ignore://忽略电池优化
                if (!WhiteListUtils.isIgnoringBatteryOptimizations(this)) {
                    WhiteListUtils.requestIgnoreBatteryOptimizations(this, REQUEST_BATTERY);
                } else {
                    toast("已添加忽略电池优化");
                }
                break;
            case R.id.btn_white://白名单
                boolean success1 = WhiteListUtils.gotoWhiteList(this);
                if (!success1) {
                    openApk("com.iqoo.secure");//打开i管家
                }
                break;
            case R.id.btn://确定
                snackbar.setAction("退出", v -> {
                    snackbar.dismiss();
                    if (AppUtils.isAppDebug()) {
                        startActivity(new Intent(activity, EnterPwdActivity.class));
                    } else {
                        onBackPressed();
                    }
                }).setActionTextColor(redTransCC99).show();
                break;
            case R.id.btn_host_manage://手机Host管理
                startActivity(new Intent(this, HostManageActivity.class));
                break;
            case R.id.btn_novel://小说
                startActivity(new Intent(this, NovelActivity.class));
                break;
            default:
                break;
        }
    }

    private void addPackageName(String packageName){
        if (!appLockDao.find(packageName)) {
            appLockDao.add(packageName);
        }
    }

    private void openApk(String packageName) {
        AppUtils.launchApp(packageName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logFormat("requestCode=%d, resultCode=%d, data=%s", requestCode, resultCode, GsonUtils.toJson(data));
        if (requestCode == REQUEST_BATTERY && resultCode == RESULT_OK) {
            toast("忽略电池优化, 设置成功!");
        }
    }
}
