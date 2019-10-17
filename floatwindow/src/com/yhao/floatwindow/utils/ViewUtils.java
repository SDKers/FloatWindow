package com.yhao.floatwindow.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description:
 * @Version: 1.0.9
 * @Create: 2017/12/29 17:15:35
 * @Author: yhao
 */
public class ViewUtils {

    private static Point sPoint;

    public static View inflate(Context applicationContext, int layoutId) {
        LayoutInflater inflate = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflate.inflate(layoutId, null);
    }

    public static int getScreenWidth(Context context) {
        if (sPoint == null) {
            sPoint = new Point();
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(sPoint);
        return sPoint.x;
    }

    public static int getScreenHeight(Context context) {
        if (sPoint == null) {
            sPoint = new Point();
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(sPoint);
        return sPoint.y;
    }

    public static boolean isViewVisible(View view) {
        return view.getGlobalVisibleRect(new Rect());
    }

    /**
     * 当前屏幕的朝向
     *
     * @return 是否是横屏
     */
    public static boolean isActivityLandscape(Activity activity) {
        Configuration c = activity.getResources().getConfiguration();
        if (c.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            return false;
        } else {
            //横屏
            return true;
        }
    }
}
