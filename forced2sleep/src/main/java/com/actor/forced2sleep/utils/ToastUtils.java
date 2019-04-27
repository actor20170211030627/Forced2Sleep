package com.actor.forced2sleep.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Kevin.
 */
public class ToastUtils {

    private static Toast toast = null;
    
    //使用主线程looper初始化handler,保证handler发送的消息运行在主线程
    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 防止了一直调用本方法后多个Toast重叠一直显示很长时间的问题
     */
    public static void show(final Context ctx, final String text) {
        //判断当前是主线程还是子线程
        //当前looper是否等于主线程looper, 如果是, 说明当前是在主线程
        if (Looper.myLooper() == Looper.getMainLooper()) {

            if (toast == null) {
                toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
            } else {
                toast.setText(text);        //防止多个Toast重叠一直显示
            }
            toast.show();

            //Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();//老师原来写法
        } else {
            //子线程
            //handler.sendEmptyMessage(0);//handler发送一个消息给队列
            //handler发送一个任务给队列
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //当Looper轮询到此任务时, 会在主线程运行此方法

                    if (toast == null) {
                        toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
                    } else {
                        toast.setText(text);        //防止多个Toast重叠一直显示
                    }
                    toast.show();

                    //Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();//老师原来写法
                }
            });
        }
    }

    /**
     * 这种Toast的方式不是单例的方式,即:你连续按几次之后,几个Toast排队.show();
     */
    public static void showDefault(final Context ctx, final String text) {
        //判断当前是主线程还是子线程
        //当前looper是否等于主线程looper, 如果是, 说明当前是在主线程
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
        } else {
            //子线程
            //handler.sendEmptyMessage(0);//handler发送一个消息给队列
            //handler发送一个任务给队列
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //当Looper轮询到此任务时, 会在主线程运行此方法
                    Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
