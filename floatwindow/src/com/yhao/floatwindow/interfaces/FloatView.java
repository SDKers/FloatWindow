package com.yhao.floatwindow.interfaces;

import android.view.View;

/**
 * Created by yhao on 17-11-14. https://github.com/yhaolpz
 */
public abstract class FloatView {

    public abstract void setSize(int width, int height);

    public abstract void setView(View view);

    public abstract void setGravity(int gravity, int xOffset, int yOffset);

    public abstract void init();

    public abstract void dismiss();

    public void updateXY(int x, int y) {}

    public void updateX(int x) {}

    public void updateY(int y) {}

    public int getX() {
        return 0;
    }

    public int getY() {
        return 0;
    }
}
