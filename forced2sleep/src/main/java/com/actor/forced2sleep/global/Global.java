package com.actor.forced2sleep.global;

/**
 * Description: 本类是存放全局变量和参数
 * Copyright  : Copyright (c) 2015
 * Company    : 公司名称
 * Date       : 2017/1/14 on 21:33.
 */

public class Global {

    //必须GET
    public static final String CHECK_UPDATE = "https://raw.githubusercontent.com/" +
            "actor20170211030627/" +
            "Forced2Sleep" +//项目名
            "/master/" +
            "forced2sleep" +//模块名
            "/build/outputs/apk/debug/output.json";

    public static final String DOWNLOAD_URL = "https://raw.githubusercontent.com/" +
            "actor20170211030627/" +
            "Forced2Sleep" +//项目名
            "/master/" +
            "forced2sleep" +//模块名
            "/build/outputs/apk/debug/app-debug.apk";

    public static final String APP_SKIP_10MIN = "APP_SKIP_10MIN";//跳过10分钟的app
}
