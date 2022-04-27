package com.actor.forced2sleep.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import com.actor.forced2sleep.global.Global;
import com.actor.forced2sleep.info.CheckUpdateInfo;
import com.actor.myandroidframework.utils.okhttputils.BaseCallback;
import com.actor.myandroidframework.utils.okhttputils.GetFileCallback;
import com.actor.myandroidframework.utils.okhttputils.MyOkHttpUtils;
import com.blankj.utilcode.util.AppUtils;

import java.io.File;
import java.util.List;

import okhttp3.Call;

/**
 * Description: 检查更新
 * 1.修改请求地址
 * 2.使用: new CheckUpdateUtils().check(this);
 *
 * Date       : 2019/10/19 on 14:39
 *
 * @version 1.0
 */
public class CheckUpdateUtils {

    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;

    //check update检查更新
    @RequiresPermission(value = Manifest.permission.REQUEST_INSTALL_PACKAGES)
    public void check(AppCompatActivity activity) {
        MyOkHttpUtils.get(Global.CHECK_UPDATE, null, new BaseCallback<CheckUpdateInfo>(activity) {
            @Override
            public void onOk(@NonNull CheckUpdateInfo info, int requestId, boolean isRefresh) {
                List<CheckUpdateInfo.ElementsBean> elements = info.elements;
                if (elements != null && !elements.isEmpty()) {
                    CheckUpdateInfo.ElementsBean elementsBean = elements.get(0);
                    if (elementsBean != null) {
                        int versionCode = AppUtils.getAppVersionCode();
                        if (versionCode < elementsBean.versionCode) {
                            showDialog((AppCompatActivity) tag, elementsBean.versionName);
                        }
                    }
                }
            }
        });
    }

    private void showDialog(AppCompatActivity activity, String newVersionName) {
        if (newVersionName == null) newVersionName = "";
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(activity)
                    .setTitle("Update: 有新版本")
                    .setMessage("有新版本: ".concat(newVersionName).concat(", 快更新吧!"))
                    .setPositiveButton("Ok", (dialog, which) -> downloadApk(activity))
                    .setNegativeButton("Cancel", null)
                    .create();
        }
        alertDialog.show();
    }

    private void downloadApk(AppCompatActivity activity) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        progressDialog.show();
        MyOkHttpUtils.getFile(Global.DOWNLOAD_URL, null, null, new GetFileCallback(activity, null) {

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                logFormat("下载文件: progress=%f, total=%d, id=%d", progress, total, id);
                progressDialog.setProgress((int) (progress * 100));
            }

            @Override
            public void onOk(@NonNull File info, int requestId, boolean isRefresh) {
                progressDialog.dismiss();
                AppUtils.installApp(info);
            }

            @Override
            public void onError(int id, Call call, Exception e) {
//                super.onError(id, call, e);
                progressDialog.dismiss();
                toast("下载失败, 请到Github下载.");
            }
        });
    }
}
