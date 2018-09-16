package com.yhao.floatwindow;

import android.view.View;

/**
 * Created by yhao on 2017/12/22. https://github.com/yhaolpz
 */
public abstract class IFloatWindow {
    // 展示
    public abstract void show();

    // 隐藏
    public abstract void hide();

    public abstract int getX();

    public abstract int getY();

    public abstract void updateX(int x);

    public abstract void updateX(@Screen.screenType int screenType, float ratio);

    public abstract void updateY(int y);

    public abstract void updateY(@Screen.screenType int screenType, float ratio);

    public abstract View getView();

    public abstract boolean isViewVisible();

    // 调整为销毁
    abstract void dismiss();
}
