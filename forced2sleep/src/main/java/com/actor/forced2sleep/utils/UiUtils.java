package com.actor.forced2sleep.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewTreeObserver;

import com.actor.myandroidframework.utils.ConfigUtils;

import java.util.Random;

/**
 * Created by zhengping on 2017/2/28,15:37.
 * 1.dp 和 像素 的互转    dp2px(用的多)  px2dp
 * 2.获取屏幕宽度/高度getScreenWidth
 */

public class UiUtils {

    public static Context getContext() {//googleplay
        return ConfigUtils.APPLICATION;
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
