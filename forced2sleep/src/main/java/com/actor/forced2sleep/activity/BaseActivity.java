package com.actor.forced2sleep.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.application.MyApplication;
import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.blankj.utilcode.util.CacheDiskUtils;
import com.jaeger.library.StatusBarUtil;


/**
 * Description: 基类
 * Date       : 2017/5/27 on 12:45.
 */
public class BaseActivity extends ActorBaseActivity {

    protected CacheDiskUtils aCache = MyApplication.instance.aCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.red_trans_CC99));
    }
}
