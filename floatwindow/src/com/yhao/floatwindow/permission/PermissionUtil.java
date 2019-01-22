package com.yhao.floatwindow.permission;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.yhao.floatwindow.utils.LogUtil;

import java.lang.reflect.Method;

/**
 * Created by yhao on 2017/12/29. https://github.com/yhaolpz
 */
public class PermissionUtil {
    public static void req(Context context) {
        if (hasPermission(context)) {
            return;
        }
        FloatActivity.request(context, new PermissionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFail() {
            }
        });
    }

    public static boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(context);
        } else {
            return hasPermissionBelowMarshmallow(context);
        }
    }

    static boolean hasPermissionOnActivityResult(Context context) {
        if (Build.VERSION.SDK_INT == 26) {
            return hasPermissionForO(context);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(context);
        } else {
            return hasPermissionBelowMarshmallow(context);
        }
    }

    /**
     * 6.0以下判断是否有权限 理论上6.0以上才需处理权限，但有的国内rom在6.0以下就添加了权限 其实此方式也可以用于判断6.0以上版本，只不过有更简单的canDrawOverlays代替
     */
    static boolean hasPermissionBelowMarshmallow(Context context) {
        try {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Method dispatchMethod = AppOpsManager.class.getMethod("checkOp", int.class, int.class, String.class);
            // AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
            return AppOpsManager.MODE_ALLOWED == (Integer) dispatchMethod.invoke(manager, 24, Binder.getCallingUid(),
                    context.getApplicationContext().getPackageName());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用于判断8.0时是否有权限，仅用于OnActivityResult 针对8.0官方bug:在用户授予权限后Settings.canDrawOverlays或checkOp方法判断仍然返回false
     */
    @SuppressWarnings("deprecation")
    @TargetApi(23)
    private static boolean hasPermissionForO(Context context) {
        try {
            WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (mgr == null) {
                return false;
            }
            View viewToAdd = new View(context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    0,
                    0,
//                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
//                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//                    : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    android.os.Build.VERSION.SDK_INT >= 26 ? 2038 : 2003,
                    0x00000010 | 0x00000008,
                    -2);
//                    PixelFormat.TRANSPARENT);
            viewToAdd.setLayoutParams(params);
            mgr.addView(viewToAdd, params);
            mgr.removeView(viewToAdd);
            return true;
        } catch (Throwable e) {
            LogUtil.e(Log.getStackTraceString(e));
        }
        return false;
    }

}
