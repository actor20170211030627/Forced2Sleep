package com.actor.forced2sleep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Description: 类的描述
 * Date       : 2018/11/15 on 23:33
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}
