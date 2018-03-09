package com.yhao.floatwindow;

import android.content.Context;
import android.widget.Toast;

/**
 * @Copyright © 2018 EGuan Inc. All rights reserved.
 * @Description: TODO
 * @Version: 1.0
 * @Create: 18/3/9 13:41
 * @Author: sanbo
 */
public class T {
    private static Toast mToast;

    public static void show(Context context, String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void hide() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
