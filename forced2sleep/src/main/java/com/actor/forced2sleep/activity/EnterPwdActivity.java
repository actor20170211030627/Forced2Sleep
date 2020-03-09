package com.actor.forced2sleep.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.global.Global;
import com.actor.forced2sleep.utils.ACache;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * 程序锁输入密码页面
 * <p>
 * 1. 解决任务栈页面跳转bug,在清单文件中手动注册页面,启动模式设置成单例设计模式
 * <p>
 * <activity
 * android:name=".activity.EnterPwdActivity"
 * android:launchMode="singleInstance"
 * android:excludeFromRecents="true"> //2. 不让输入密码页面进入系统最近任务列表中,
 * //如果这个Activity是整个Task的根Activity，整个Task将不会出现在最近任务列表中。
 * </activity>
 */
public class EnterPwdActivity extends BaseActivity {

    @BindView(R.id.iv_icon)//图标
    ImageView ivIcon;
    @BindView(R.id.tv_name)//名称
    TextView tvName;
    @BindView(R.id.tv_package_name)//包名
    TextView tvPackageName;
    @BindView(R.id.tv_count_down)//倒计时
    TextView       tvCountDown;
    @BindView(R.id.tv_md5)//显示的md5
    TextView       tvMd5;
    @BindView(R.id.tv_pwd)//需要输入的密码
    TextView       tvPwd;

    private        String         packageName;
    private        int            passwordLength;//密码长度
    private        Timer          timer = new Timer();//删除
    private        CountDownTimer countDownTimer;
    //    private boolean isInputBack = false;//是否需要倒叙输入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        ButterKnife.bind(this);

        passwordLength = getResources().getInteger(R.integer.passwordLength);
        init();
        initIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initIntent();
    }

    private void initIntent() {
        //获取AppLockService.java发过来的包名
        packageName = getIntent().getStringExtra(Global.PACKAGE_NAME);
        tvPackageName.setText(packageName);
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            String name = applicationInfo.loadLabel(pm).toString();
            Drawable icon = applicationInfo.loadIcon(pm);
            tvName.setText(name);
            ivIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    //初始化 & 输错后重写初始化 & 输入为空
    private void init() {
        tvMd5.setText(getMd5String());
//        isInputBack = new Random().nextBoolean();
        //tvPwd.setHint("请输入"+passwordLength+"位年月日时分MD5密码");
//        if (isInputBack) {
//            tvPwd.setHint("请倒序输入上面所有数字");
//        } else tvPwd.setHint("请输入上面所有数字");

        //必须new才能重置计时...
        countDownTimer = new CountDownTimer(getCountDownSecond() * 1000, 500) {

            @Override
            public void onTick(long millisUntilFinished) {
                tvCountDown.setText(getStringFormat("%02d秒后重置密码", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvCountDown.setText("0秒后重置密码");
                init();
            }
        }.start();
    }

    @OnClick({R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,R.id.btn7,
            R.id.btn8, R.id.btn9, R.id.btn_ok, R.id.btn_delete})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok://确定
                String text = getText(tvPwd);
                if (!TextUtils.isEmpty(text)) {
                    if (text.equals(getStringNum(getMd5String(), passwordLength))) {
                        aCache.put(packageName, packageName, ACache.TIME_MINUTE * 1);
                        toast("这个App有效时间为1分钟");
                        onBackPressed();
                    } else {
                        init();
                        toast("密码错误:" + tvPwd.getHint());
                    }
                } else {
                    init();
                    toast("密码为空:" + tvPwd.getHint());
                }
                break;
            case R.id.btn_delete://删除
                deleteText();
                break;
            default://0-9
                tvPwd.append(((Button) v).getText());
                break;
        }
    }
    @OnLongClick(R.id.btn_delete)
    public boolean onLongClick(View view) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (view.isPressed()) {
                        deleteText();
                    } else cancel();//如果不是长按,就取消任务
                });
            }
        }, 0, 50);
        return true;
    }

    //删除文字
    private void deleteText() {
        String s = tvPwd.getText().toString();
        if (s.length() > 0) tvPwd.setText(s.substring(0, s.length() - 1));
    }

    /**
     * 获取倒计时秒数
     * @return 0-59
     */
    private int getCountDownSecond() {
        return 60 - Calendar.getInstance().get(Calendar.SECOND);
    }

    //获取现在时间md5
    private String getMd5String() {
        Calendar calendar = Calendar.getInstance();
        String time = TimeUtils.date2String(calendar.getTime(), "yyyyMMddHHmm");
        return EncryptUtils.encryptMD5ToString(time);
    }

    //提取字符串中数字
    private String getStringNum(String string, int maxLentgh) {
        char[] chars = string.toCharArray();
        String needReturn = "";
        if (false) {//倒序isInputBack
            for (int i = chars.length - 1; i >= 0; i--) {
                if (chars[i] >= '0' && chars[i] <= '9') {
                    needReturn += chars[i];
                }
            }
        } else {
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] >= '0' && chars[i] <= '9') {
                    needReturn += chars[i];
                }
            }
        }
        if (needReturn.length() > maxLentgh) {
            needReturn = needReturn.substring(0, maxLentgh);
        }
        return needReturn;
    }

    private void JumpToDesk() {
        ActivityUtils.startHomeActivity();
        //跳到桌面
//        Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
    }

    //拦截手机物理返回键
    @Override
    public void onBackPressed() {
        JumpToDesk();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();//取消后再调用timer.schedule()会报错
        countDownTimer.cancel();
    }
}
