package com.yhao.floatwindow.enums;

/**
 * 手机旋转类型.
 * </p>
 * T_ACTIVITY_ROTATE: activity lifecycle 监听的页面屏幕旋转
 * T_SENSOR_ROTATE: 传感器监听的手机旋转
 */
public enum ETypeRotateChange {
    // 页面回调，监听的页面旋转(此时手机旋转无效)
    T_ACTIVITY_ROTATE,
    // 跳出app后，监听的手机旋转(页面旋转失效)
    T_SENSOR_ROTATE
}
