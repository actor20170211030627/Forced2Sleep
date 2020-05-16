package com.actor.forced2sleep.global;

import java.util.Calendar;

/**
 * Description: 本类是存放全局变量和参数
 * Company    : 公司名称
 * Date       : 2017/1/14 on 21:33.
 */

public class Global {

    public static final String CHECK_UPDATE = "https://gitee.com/actor20170211030627/" +
            "Forced2Sleep" +//项目名
            "/raw/master/" +
            "forced2sleep" +//模块名
            "/build/outputs/apk/debug/output.json";

    public static final String DOWNLOAD_URL = "https://raw.githubusercontent.com/" +
            "actor20170211030627/" +
            "Forced2Sleep" +
            "/master/" +
            "forced2sleep" +
            "/build/outputs/apk/debug/" +
            "forced2sleep" +
            "-debug.apk";

    //是否是'睡觉/午休'时间
    public static boolean isSleepTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //[00:00, 07:00) || [22:00, 23:59]
        if (hour < 7 || hour >= 22) {
            return true;
        }

        //[07:00 ~ 22:00)
        switch (hour) {
            case 7:
                return minute < 30;//[07:00 ~ 07:30)
            case 13:
                return minute < 30;//[13:00 ~ 13:30)
            case 22:
                return minute > 30;//(22:30 ~ 23:00]
            default:
                return false;
        }
    }

    public static final String APP_SKIP_10MIN = "APP_SKIP_10MIN";//跳过10分钟的app
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
}
