package com.yhao.floatwindow.utils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.yhao.floatwindow.interfaces.ISensorRotateChanged;

import java.lang.ref.WeakReference;

/**
 * @Copyright © 2019 sanbo Inc. All rights reserved.
 * @Description: 方向感应
 * @Version: 1.0
 * @Create: 2019-10-16 18:10:38
 * @author: sanbo
 */
public class RotateUtil {

    private SensorManager mSenserManger = null;
    private OrientationSensorListener mSensorListener = null;
    private Sensor mSensor = null;
    // 多次初始化
    private boolean isInit = false;
    // 是否中断, 默认不中断
    private boolean isInterrupt = false;
    // 默认是竖屏
    private boolean isLandscape = false;
    // 记录点击全屏后屏幕朝向是否改变，默认会自动切换
    private boolean isChangeOrientation = true;
    // 为了给页面传递方向改变
    private WeakReference<Activity> mActivityRef = null;
    // 变动时回调
    private ISensorRotateChanged mSensorRotateChanged = null;


    private RotateUtil() {
    }

    public static RotateUtil getInstance() {
        return HOLDER.INSTANCE;
    }

    public void start(Activity activity) {
        initSensorManager(activity.getApplicationContext());
        if (!isInit && mSenserManger != null) {
            mSenserManger.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (activity != null || mActivityRef.get() == null) {
            mActivityRef = new WeakReference<Activity>(activity);
        }
    }

    public void stop() {
        if (isInit && mSenserManger != null) {
            mSenserManger.unregisterListener(mSensorListener);
        }
        if (mActivityRef != null || mActivityRef.get() != null) {
            mActivityRef = null;
        }
    }

    /**
     * 初始化传感器
     *
     * @param context
     */
    private void initSensorManager(Context context) {
        // 初始化重力感应器
        if (mSenserManger == null) {
            mSenserManger = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        if (mSensor == null) {
            mSensor = mSenserManger.getDefaultSensor(Sensor.TYPE_GRAVITY);
        }
        if (mSensorListener == null) {
            mSensorListener = new OrientationSensorListener();
        }
    }

    /**
     * 设置回调
     *
     * @param sensorRotateChanged
     */
    public void setRotateChanged(ISensorRotateChanged sensorRotateChanged) {
        if (sensorRotateChanged != null) {
            mSensorRotateChanged = sensorRotateChanged;
        }
    }

    /**
     * 当前屏幕朝向是否横屏
     *
     * @param orientation
     * @return
     */
    private boolean screenIsLandscape(int orientation) {
        return ((orientation > 45 && orientation <= 135) || (orientation > 225 && orientation <= 315));
    }

    /**
     * 当前屏幕朝向是否竖屏
     *
     * @param orientation
     * @return
     */
    private boolean screenIsPortrait(int orientation) {
        return (((orientation > 315 && orientation <= 360) || (orientation >= 0 && orientation <= 45))
                || (orientation > 135 && orientation <= 225));
    }

    private static class HOLDER {
        private static RotateUtil INSTANCE = new RotateUtil();
    }

    /**
     * 重力感应监听者
     */
    public class OrientationSensorListener implements SensorEventListener {
        public static final int ORIENTATION_UNKNOWN = -1;
        private static final int DATA_X = 0;
        private static final int DATA_Y = 1;
        private static final int DATA_Z = 2;
        //上次是否竖屏
        private boolean isLastLandscape = false;

        @Override
        public void onSensorChanged(SensorEvent event) {

            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[DATA_X];
            float Y = -values[DATA_Y];
            float Z = -values[DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }

            //竖屏
            if (screenIsPortrait(orientation)) {
                //上次非竖屏,屏幕旋转
                if (!isLastLandscape) {
                    isChangeOrientation = true;
                    if (FwContent.isDebug) {
                        L.v("onSensorChanged: 横屏 ----> 竖屏");
                    }
                } else {
                    isChangeOrientation = false;
                    if (FwContent.isDebug) {
                        L.v("onSensorChanged: 竖屏 ----> 竖屏");
                    }
                }
                isLastLandscape = true;
                // 横屏处理
            } else if (screenIsLandscape(orientation)) {
                //上次竖屏,屏幕旋转
                if (isLastLandscape) {
                    isChangeOrientation = true;
                    if (FwContent.isDebug) {
                        L.v("onSensorChanged: 竖屏 ---->横屏");
                    }
                } else {
                    isChangeOrientation = false;
                    if (FwContent.isDebug) {
                        L.v("onSensorChanged: 横屏 ----> 横屏");
                    }
                }
                isLastLandscape = false;
            }

            // 页面变化，回调
            if (isChangeOrientation) {
                mSensorRotateChanged.onRotateChanged();
                isChangeOrientation = false;
            }

            // 不拦截时，对应页面也可以收到页面的改变

            if (!isInterrupt && mActivityRef != null && mActivityRef.get() != null) {
                // 根据手机屏幕的朝向角度，来设置内容的横竖屏，并且记录状态
                int requestedOrientation = 0;

                if (orientation > 45 && orientation < 135) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (orientation > 135 && orientation < 225) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else if (orientation > 225 && orientation < 315) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }
                // 接收重力感应监听的结果，来改变屏幕朝向
                mActivityRef.get().setRequestedOrientation(requestedOrientation);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    }

}
