package com.actor.forced2sleep.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Process;
import com.actor.forced2sleep.utils.ACache;

/**
 * Created by zhengping on 2017/4/2,10:03.
 *
 * 1、生命周期长
 *      存一些东西（退出应用程序按钮）
 *      内存泄露：生命周期短的被生命周期长的对象长期引用
 * 2、单例（一个进程只有一个Application的实例对象）
 * 3、onCreate方法是一个应用程序入口
 *
 *
 * 4、注意：自定义的Application需要在清单文件中注册
 *
 */

public class MyApplication extends Application {

    public static MyApplication instance;
    public        Context       applicationContext;
    public        Handler       handler;
    public        int           mainThreadId;
    public        ACache        aCache;
    public        boolean       isDebugMode;//是否是debug模式

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //ImageLoader  环信   BMob初始化

        //常见的东西可以在这里初始化
        // new View()   startActivity  Toast
        //获取应用程序内部的存储空间
        // sp;
        //1、初始化全局的ApplicationContext
        applicationContext = getApplicationContext();
        //2、线程间的通信   Handler
        //Handler：发送消息  Handler.sendMessage
        //处理消息  handleMessage
        //任何一个线程都有消息队列 Handler.sendMessage将一个消息发送到Handler所维护的消息队列中
        //如何让一个Handler维护的是主线程的消息队列
        //1、在主线程中new出来  2、在任何一个地方指定new Handler(Looper.getMainLooper())
        handler = new Handler();
        /*new Thread(

                new Runnable() {
                    private Handler subThreadHandler;
                    @Override
                    public void run() {
                        //Looper.prepare();
                        subThreadHandler = new Handler(Looper.getMainLooper());
                       // Looper.loop();
                    }
                }
        ).start();*/

        //3、获取主线程的线程id  判断是否是主线程
        /*if(Looper.getMainLooper() == Looper.myLooper()) {
            //主线程
        }else {
            //子线程
        }*/
        //myTid这个方法在哪里被调用，它的返回值就是这个方法所在线程的id
        mainThreadId = Process.myTid();
        aCache = ACache.get(this);
        isDebugMode = getMode();
    }

    /**
     * 当我们没在AndroidManifest.xml中设置其debug属性时:
     * 使用Eclipse运行这种方式打包时其debug属性为true,使用Eclipse导出这种方式打包时其debug属性为法false.
     * 在使用ant打包时，其值就取决于ant的打包参数是release还是debug.
     * 因此在AndroidMainifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     *
     * 如果release版本也想输出日志，那么这个时候我们到 AndroidManifest.xml 中的application
     * 标签中添加属性强制设置debugable即可:
     * <application android:debuggable="true" tools:ignore="HardcodedDebugMode"
     */
    private boolean getMode(){
        try {
            ApplicationInfo info= getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) !=0 ;
        } catch (Exception e) {
            return false;
        }
    }
}
