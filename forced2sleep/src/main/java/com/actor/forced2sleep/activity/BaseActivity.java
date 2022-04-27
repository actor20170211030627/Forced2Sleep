package com.actor.forced2sleep.activity;

import androidx.viewbinding.ViewBinding;

import com.actor.forced2sleep.application.MyApplication;
import com.actor.myandroidframework.activity.ViewBindingActivity;
import com.blankj.utilcode.util.CacheDiskUtils;


/**
 * Description: 基类
 * Date       : 2017/5/27 on 12:45.
 */
public class BaseActivity<VB extends ViewBinding> extends ViewBindingActivity<VB> {

    protected CacheDiskUtils aCache = MyApplication.instance.aCache;
}
