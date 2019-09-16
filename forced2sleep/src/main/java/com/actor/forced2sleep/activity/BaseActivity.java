package com.actor.forced2sleep.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.actor.forced2sleep.R;
import com.actor.forced2sleep.application.MyApplication;
import com.actor.forced2sleep.utils.ACache;
import com.actor.forced2sleep.utils.ToastUtils;


/**
 * Description: 类的功能描述
 * Date       : 2017/5/27 on 12:45.
 */
public class BaseActivity extends AppCompatActivity {

    protected ACache aCache = MyApplication.instance.aCache;

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.next_enter, R.anim.pre_exit);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pre_enter, R.anim.next_exit);
    }

    public void toast(String notify){
        ToastUtils.show(this,notify);
    }

    //判断EditText是否为空
    public boolean isNoEmpty(EditText editText, String nofify) {
        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
            ToastUtils.show(this, nofify);
            return false;
        }
        return true;
    }

    protected String getText(TextView textView){
        return textView.getText().toString();
    }
}
