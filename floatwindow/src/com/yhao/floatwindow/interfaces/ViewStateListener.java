package com.yhao.floatwindow.interfaces;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description:
 * @Version: 1.0.9
 * @Create: 2017-11-14 17:15:35
 * @Author: yhao
 */
public interface ViewStateListener {
    public void onPositionUpdate(int x, int y);

    public void onShow();

    public void onHide();

    public void onDismiss();

    public void onMoveAnimStart();

    public void onMoveAnimEnd();

    public void onBackToDesktop();
}
