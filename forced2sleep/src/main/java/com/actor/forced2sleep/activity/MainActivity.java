package com.actor.forced2sleep.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.db.AppLockDao;
import com.actor.forced2sleep.service.AppLockService;
import com.actor.forced2sleep.utils.AccessibilityUtils;
import com.actor.forced2sleep.utils.ServiceStateUtils;
import com.actor.forced2sleep.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

public class MainActivity extends BaseActivity {

    private Button btn;
    private Snackbar snackbar;
    private Intent intent;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setTransparentForImageView(this, null);
        activity = this;
        intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                ToastUtils.showDefault(this, data.getAuthority());
                System.out.println(data.getAuthority());//传递的内容
            } else {
                System.out.println("data == null,from SplashActivity");
            }
        }

        /**
         * 下面这些是白名单
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
        addPackageName("com.bbk.calendar");//日历
        addPackageName("com.bbk.launcher2");//桌面
        addPackageName("com.bbk.SuperPowerSave");//超级省电界面
//        addPackageName("com.iqoo.powersaving");//超级省电
        //addPackageName("com.iqoo.secure");//通话详情-->加入黑名单&隐私通讯&骚扰拦截&i管家
        addPackageName("com.actor.forced2sleep");//把自己加进去
//        addPackageName("com.vivo.browser");//浏览器
        addPackageName("com.vivo.gallery");//相机相册

        addPackageName("com.autonavi.minimap");//高德地图
        addPackageName("com.eg.android.AlipayGphone");//支付宝
        addPackageName("com.kuchuan.getsign");//酷川云获取签名
        addPackageName("com.starrymedia.metro.best");//最地铁
        addPackageName("com.tc.cm");//地铁通
        addPackageName("com.tencent.androidqqmail");//腾讯邮箱
        addPackageName("com.tencent.mm");//微信
        addPackageName("com.tencent.mobileqq");//QQ
        addPackageName("longbin.helloworld");//计算器

        btn = (Button) findViewById(R.id.btn);
        snackbar = Snackbar.make(btn, "color:#cc99", Snackbar.LENGTH_INDEFINITE);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                snackbar.setAction("退出", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                        finish();
                    }
                })
                        .setActionTextColor(getResources().getColor(R.color.pink_cc99))
                        .show();
            }
        });
        findViewById(R.id.btn_launch).setOnClickListener(new View.OnClickListener() {//开机启动
            @Override
            public void onClick(View v) {
//                intent = new Intent()
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        .setAction("android.settings.APPLICATION_DETAILS_SETTINGS")
//                        .setData(Uri.fromParts("package", getPackageName(), null));
////                        .setComponent(ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.PurviewTabActivity"));
//                startActivity(intent);

                toast("开机启动(未完成,需自己开启)");
            }
        });
        findViewById(R.id.btn_start_fuzhu).setOnClickListener(new View.OnClickListener() {//开启辅助功能
            @Override
            public void onClick(View v) {
                if (!AccessibilityUtils.isAccessibilitySettingsOn(AppLockService.class)) {
//                    if (!ServiceStateUtils.isServiceRunning(this, AppLockService.class)) {
//                    if (!ServiceStateUtils.isAccessibilityRunning(new AppLockService())) {
                    AccessibilityUtils.openAccessibility(activity);
                    ToastUtils.showDefault(activity, "请开启辅助功能");
                } else {
                    ToastUtils.showDefault(activity, "辅助功能已开启");
//                    openApk("com.iqoo.secure");//打开i管家
                }
                System.out.println("AppLockService运行状态:" + String.valueOf(ServiceStateUtils.isServiceRunning(activity, AppLockService.class)));
                if (!ServiceStateUtils.isServiceRunning(activity, AppLockService.class)) {
                    startService(new Intent(activity, AppLockService.class));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private AppLockDao appLockDao = AppLockDao.getInstance(this);
    private void addPackageName(String packageName){
        if (!appLockDao.find(packageName)) {
            appLockDao.add(packageName);
        }
    }

    private void openApk(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        // 启动目标应用
        startActivity(intent);
    }

    //屏幕锁屏解锁监听
}
