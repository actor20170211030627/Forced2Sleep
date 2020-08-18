package com.actor.forced2sleep.activity;

import com.actor.forced2sleep.application.MyApplication;
import com.actor.myandroidframework.activity.ActorBaseActivity;
import com.blankj.utilcode.util.CacheDiskUtils;


/**
 * Description: 基类
 * Date       : 2017/5/27 on 12:45.
 */
public class BaseActivity extends ActorBaseActivity {

    protected CacheDiskUtils aCache = MyApplication.instance.aCache;
}
