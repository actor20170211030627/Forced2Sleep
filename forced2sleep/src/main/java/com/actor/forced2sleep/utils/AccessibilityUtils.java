package com.actor.forced2sleep.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.actor.myandroidframework.utils.ConfigUtils;

import java.io.File;
import java.util.List;

/**
 * Created by popfisher on 2017/7/11.
 */
public class AccessibilityUtils {

    private static Context context = ConfigUtils.APPLICATION;
    protected static AccessibilityService service;

    private AccessibilityUtils() {
        throw new RuntimeException(getClass().getName() + " can not be new!");
    }

    /**
     * Service 创建的时候
     */
    public static void onCreate(AccessibilityService service) {
        AccessibilityUtils.service = service;
    }

    /**
     * Service 销毁的时候
     */
    public static void onDestroy() {
        AccessibilityUtils.service = null;
    }

    private static AccessibilityNodeInfo getRootNodeInfo(AccessibilityEvent event) {
        if (Build.VERSION.SDK_INT >= 16) {
            // 建议使用getRootInActiveWindow，这样不依赖当前的事件类型
            return service.getRootInActiveWindow();
        } else {
            return event.getSource();
        }
    }

    /**
     * 根据Text搜索所有符合条件的节点, 模糊搜索方式
     */
    public static List<AccessibilityNodeInfo> findNodesByText(AccessibilityEvent event, String text) {
        AccessibilityNodeInfo nodeInfo = getRootNodeInfo(event);
        if (nodeInfo != null) {
           return nodeInfo.findAccessibilityNodeInfosByText(text);
        }
        return null;
    }

    /**
     * 根据View的ID搜索符合条件的节点,精确搜索方式;
     * 这个只适用于自己写的界面，因为ID可能重复
     * api要求18及以上
     * @param viewId 要加上包名,示例:com.google.example:id/cb_checkbox
     */
    public static List<AccessibilityNodeInfo> findNodesById(AccessibilityEvent event, String viewId) {
        AccessibilityNodeInfo nodeInfo = getRootNodeInfo(event);
        if (nodeInfo != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                return nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
            }
        }
        return null;
    }

    public static boolean clickByText(AccessibilityEvent event, String text) {
        return performClick(findNodesByText(event, text));
    }

    /**
     * 根据View的ID搜索符合条件的节点,精确搜索方式;
     * 这个只适用于自己写的界面，因为ID可能重复
     * api要求18及以上
     * @param viewId
     * @return 是否点击成功
     */
    public static boolean clickById(AccessibilityEvent event, String viewId) {
        return performClick(findNodesById(event, viewId));
    }

    /**
     * 模拟点击
     */
    private static boolean performClick(List<AccessibilityNodeInfo> nodeInfos) {
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            for (AccessibilityNodeInfo node : nodeInfos) {
                // 获得点击View的类型
                System.out.println("View类型：" + node.getClassName());
                // 进行模拟点击
                if (node.isEnabled()) {
                    return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
        return false;
    }

    /**
     * 模拟点击返回键
     */
    public static boolean clickBackKey() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    /**
     * 模拟action所对应事件
     */
    private static boolean performGlobalAction(int action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return service.performGlobalAction(action);
        }
        return false;
    }

    /**
     * 判断是否有辅助功能权限
     */
    public static boolean isAccessibilitySettingsOn(Class<? extends AccessibilityService> service) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext()
                    .getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessibilityEnabled == 1) {
            //原来是这么写的:getClass().getCanonicalName()
            String serviceStr = context.getPackageName() + File.separator + service.getName();
            TextUtils.SimpleStringSplitter stringSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(context.getApplicationContext()
                    .getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                stringSplitter.setString(settingValue);
                while (stringSplitter.hasNext()) {
                    String accessabilityService = stringSplitter.next();
                    System.out.println("accessabilityService: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(serviceStr)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 判断当前辅助功能服务是否正在运行, 这儿来自抢红包app里的判断!
     * */
    @Deprecated
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isAccessibilityRunning(Class<? extends AccessibilityService> serviceClass) {
        if(serviceClass == null) {
            return false;
        }
        AccessibilityManager manager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        //明明已经开启了辅助功能, 这儿list还是empty
        List<AccessibilityServiceInfo> list = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        //accessibilityServiceInfo 第2次判断的时候会报错. 就算直接new 也会这样...
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        for (AccessibilityServiceInfo info : list) {
            if (TextUtils.equals(info.getId(), accessibilityServiceInfo.getId())) {
                return true;
            }
        }
        return false;
    }
    private static AccessibilityServiceInfo accessibilityServiceInfo;
    private static AccessibilityServiceInfo getAccessibilityServiceInfo(){
        if (accessibilityServiceInfo == null) {
            accessibilityServiceInfo = new AccessibilityServiceInfo();
        }
        return accessibilityServiceInfo;
    }

    /**
     * 跳转到系统设置页面开启辅助功能
     */
    public static void openAccessibility(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 获取打开辅助功能的Intent
     */
    public static Intent getAccessibilitySettingIntent() {
        return new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    }
}
