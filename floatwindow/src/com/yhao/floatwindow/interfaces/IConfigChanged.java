package com.yhao.floatwindow.interfaces;

/**
 * @Copyright © 2019 sanbo Inc. All rights reserved.
 * @Description: 页面旋转回调
 * @Version: 1.0
 * @Create: 2019-10-15 14:47:56
 * @author: sanbo
 */
public interface IConfigChanged {
    //页面旋转时回调
    public abstract void onActivityConfigChanged();

    // 跳出应用、打开应用回调，用于旋转优先级选择。
    public abstract void onBackToDesktop(boolean isBack);
}
