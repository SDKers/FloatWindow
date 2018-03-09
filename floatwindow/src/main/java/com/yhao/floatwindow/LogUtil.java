package com.yhao.floatwindow;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yhao on 2017/12/29.
 * https://github.com/yhaolpz
 */
public class LogUtil {

    private static final String TAG = "FloatWindow";


    public static void e(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.e(TAG, message);
        }

    }

    public static void v(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.v(TAG, message);
        }

    }


    public static void i(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.i(TAG, message);
        }
    }

    public static void w(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.w(TAG, message);
        }
    }


    public static void d(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.d(TAG, message);
        }

    }

    public static void wtf(String message) {
        List<String> list = split(message);
        for (String str : list) {
            Log.wtf(TAG, message);
        }

    }

    /**
     * 支持超大字符串
     *
     * @param msg
     */
    private static List<String> split(String msg) {
        List<String> arr = new ArrayList<String>();
        if (msg.length() > 4000) {
            int chunkCount = msg.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= msg.length()) {
                    arr.add(msg.substring(4000 * i));
                } else {
                    arr.add(msg.substring(4000 * i, max));
                }
            }
        } else {
            arr.add(msg);
        }
        return arr;
    }

}
