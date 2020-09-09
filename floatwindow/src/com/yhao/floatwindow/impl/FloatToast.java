package com.yhao.floatwindow.impl;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.yhao.floatwindow.interfaces.BaseFloatView;
import com.yhao.floatwindow.utils.RefInvoke;

import java.lang.reflect.Method;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description: 自定义 toast 方式，无需申请权限 当前版本暂时用 TYPE_TOAST 代替，后续版本可能会再融入此方式
 * @Version: 1.0.9
 * @Create: 2017-11-14 17:15:35
 * @Author: yhao
 */
public class FloatToast extends BaseFloatView {

    private Toast toast;

    private Object mTN;
    private Method show;
    private Method handleShow;
    private Method hide;

    private int mWidth;
    private int mHeight;

    public FloatToast(Context applicationContext) {
        toast = new Toast(applicationContext);
    }

    @Override
    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void setView(View view) {
        toast.setView(view);
        initTN();
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity, xOffset, yOffset);
    }

    @Override
    public void init() {
        try {
            if (show != null) {
                show.invoke(mTN);
            } else {
                toast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            hide.invoke(mTN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initTN() {
        try {
    
            mTN = RefInvoke.getFieldObject(toast, "mTN");
            final Handler originHandler = (Handler) RefInvoke.getFieldObject(mTN, "mHandler");
            show = RefInvoke.getMethod(mTN, "show");
            if (show == null) {
                handleShow = RefInvoke.getMethod(mTN, "handleShow", new Class[]{IBinder.class});
                RefInvoke.setFieldObject(mTN, "mHandler", new Handler() {
                    @Override
                    public void dispatchMessage(Message msg) {
                        try {
                            if (msg.what == 0) {
                                IBinder token = (IBinder) msg.obj;
//                        mShow.invoke()
                                handleShow.invoke(mTN, token);
                                return;
                            }
                            originHandler.dispatchMessage(msg);
                        } catch (Exception e) {
                            // ignore
                        }
                    }
            
                    @Override
                    public void handleMessage(Message msg) {
                        try {
                            if (msg.what == 0) {
                                IBinder token = (IBinder) msg.obj;
                                handleShow.invoke(mTN, token);
                                return;
                            }
                            originHandler.handleMessage(msg);
                        } catch (Exception igone) {
                        }
                    }
                });
            }
            hide = RefInvoke.getMethod(mTN, "hide");
            // 更新无效，可以显示，不可以长时间显示
//                RefInvoke.getInstance().setFieldValue(mTN, "mDuration", 999999);
//                RefInvoke.getInstance().setFieldValue(mTN, "SHORT_DURATION_TIMEOUT", 999999);
//                RefInvoke.getInstance().setFieldValue(mTN, "SHORT_DURATION_TIMEOUT", 999999);
    
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) RefInvoke.getFieldObject(mTN, "mParams");
            params.flags = 0x00000020 | 0x00000008 | 0x00000100 | 0x00010000;
            params.width = mWidth;
            params.height = mHeight;
            params.windowAnimations = 0;
            RefInvoke.setFieldObject(mTN, "mNextView", toast.getView());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
