package com.yhao.floatwindow.interfaces;

import android.view.View;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description:
 * @Version: 1.0.9
 * @Create: 2017-11-14 17:15:35
 * @Author: yhao
 */
public abstract class BaseFloatView {

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
