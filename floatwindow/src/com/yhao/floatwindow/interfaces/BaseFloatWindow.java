package com.yhao.floatwindow.interfaces;

import android.view.View;

import com.yhao.floatwindow.annotation.Screen;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description: https://github.com/yhaolpz
 * @Version: 1.0
 * @Create: 2017/12/22 17:05:41
 * @Author: yhao
 */
public abstract class BaseFloatWindow {
    public abstract void show();

    public abstract void hide();

    public abstract boolean isShowing();

    public abstract int getX();

    public abstract int getY();

    public abstract void updateX(int x);

    public abstract void updateX(@Screen.screenType int screenType, float ratio);

    public abstract void updateY(int y);

    public abstract void updateY(@Screen.screenType int screenType, float ratio);

    public abstract View getView();

    public abstract void dismiss();

    public abstract void destory();
}
