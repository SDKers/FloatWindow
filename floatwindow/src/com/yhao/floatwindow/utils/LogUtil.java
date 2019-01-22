package com.yhao.floatwindow.utils;

import android.util.Log;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description:
 * @Version: 1.0.9
 * @Create: 2017/12/29 17:15:35
 * @Author: yhao
 */
public class LogUtil {

    private static final String TAG = "FloatWindow";

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void v(String message) {
        Log.v(TAG, message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
    }

    public static void wtf(String message) {
        Log.wtf(TAG, message);
    }

    public static void d(String message) {

        Log.d(TAG, message);
    }

}
