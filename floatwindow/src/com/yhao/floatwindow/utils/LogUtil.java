package com.yhao.floatwindow.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhao on 2017/12/29. https://github.com/yhaolpz
 */
public class LogUtil {

    private static final String TAG = "FloatWindow";

    public static void e(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.e(TAG, str);
        }

    }

    public static void v(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.v(TAG, str);
        }

    }

    public static void i(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.i(TAG, str);
        }
    }

    public static void w(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.w(TAG, str);
        }
    }

    public static void d(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.d(TAG, str);
        }

    }

    public static void wtf(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.wtf(TAG, str);
        }

    }

    // 规定每段显示的长度.每行最大日志长度 (Android Studio3.1最多2902字符)
    private static final int maxLen = 2900;

    /**
     * 支持超大字符串
     *
     * @param msg
     */
    private static List<String> split(String msg) {
        List<String> arr = new ArrayList<String>();
        if (msg.length() > maxLen) {
            int chunkCount = msg.length() / maxLen; // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = maxLen * (i + 1);
                if (max >= msg.length()) {
                    arr.add(msg.substring(maxLen * i));
                } else {
                    arr.add(msg.substring(maxLen * i, max));
                }
            }
        } else {
            arr.add(msg);
        }
        return arr;
    }

}
