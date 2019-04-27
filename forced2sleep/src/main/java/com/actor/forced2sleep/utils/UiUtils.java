package com.actor.forced2sleep.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;

import com.actor.forced2sleep.application.MyApplication;

import java.util.Random;

/**
 * Created by zhengping on 2017/2/28,15:37.
 * 1.dp 和 像素 的互转    dp2px(用的多)  px2dp
 * 2.获取屏幕宽度/高度getScreenWidth
 */

public class UiUtils {

    public static Context getContext() {//googleplay
        return MyApplication.instance.applicationContext;
    }

    public static Handler getMainThreadHander() {//googleplay
        return MyApplication.instance.handler;
    }

    public static int getMainThreadId() {//googleplay
        return MyApplication.instance.mainThreadId;
    }
	    //获取字符串资源
    public static String getString(int resId) {//googleplay
        return getContext().getResources().getString(resId);
    }
	    //获取字符串数组
    public static String[] getStringArray(int resId) {//googleplay
        return getContext().getResources().getStringArray(resId);
    }
	    //获取Drawable
    public static Drawable getDrawable(int resId) {//googleplay
        return getContext().getResources().getDrawable(resId);
    }

    //获取color
    public static int getColor(int resId) {//googleplay
        return getContext().getResources().getColor(resId);
    }

	public static ColorStateList getColorStateList(int resId) {//googleplay
        return getContext().getResources().getColorStateList(resId);
    }

	public static int getRandomColor() {
        //颜色随机，也不能够太随机，应该控制在一定的范围之内   30~220
        Random random = new Random();
        int red = 30 + random.nextInt(191);
        int green = 30 + random.nextInt(191);
        int blue = 30 + random.nextInt(191);
        return Color.rgb(red, green, blue);
    }

	public static int getRandomTextSize() {
        //16~25sp之间
        Random random = new Random();
        return 16 + random.nextInt(10);
    }

    public static int getDimen(int resId) {//googleplay
        return getContext().getResources().getDimensionPixelSize(resId);
    }
	public static boolean isRunOnUiThread() {//googleplay
        //是否运行在主线程
       /* if(Looper.myLooper() == Looper.getMainLooper()) {

        } else {

        }*/
        //使用线程id的比较
        int mainThreadId = getMainThreadId();
        //当前线程的id
        int currentThreadId = android.os.Process.myTid();
        return mainThreadId == currentThreadId;
    }

    /**
     * dp 转换为 像素,传入"dp",输出"像素",java代码中一般用这种
     */
    public static int dp2px(Context context,int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5f);//四舍五入
        //   3.1   --> 3
        //  3.7   --> 3
        //3.6-->3
        //4.2-->4
    }

    /**
     * 像素 转换为 dp,传入"像素",输出"dp"
     */
    public static int px2dp(Context context,int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px/density + 0.5F);
    }

    //字体的sp 转换为 sp
    public static int sp2px(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return (int)(var1 * var2 + 0.5F);
    }

    //px 转换为 sp
    public static int px2sp(Context var0, float var1) {
        float var2 = var0.getResources().getDisplayMetrics().scaledDensity;
        return (int)(var1 / var2 + 0.5F);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreemHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取控件宽度
     */
    public static int[] getViewSize(final View view){
        //我们可以对view监听视图树
        final int[] size = {0,0};
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                size[0] = view.getWidth();
                size[1] = view.getHeight();
            }
        });
        return size;
    }

	//xml文件中的shape
    public static GradientDrawable getShape(int radius, int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(radius);
        shape.setColor(color);
        return shape;
    }

    //xml文件中selector
    public static StateListDrawable getSelector(Drawable pressedDrawable, Drawable normalDrawable) {
        StateListDrawable stateListDrawable = new StateListDrawable();//代表写了一个selector的标签
        //给selector增加规则
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed},pressedDrawable);//增加按下的规则
        stateListDrawable.addState(new int[]{},normalDrawable);//增加默认的规则
        return stateListDrawable;
    }
}
