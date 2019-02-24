package com.yhao.floatwindow.impl;

import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.annotation.MoveType;
import com.yhao.floatwindow.annotation.Screen;
import com.yhao.floatwindow.interfaces.BaseFloatView;
import com.yhao.floatwindow.interfaces.BaseFloatWindow;
import com.yhao.floatwindow.interfaces.LifecycleListener;
import com.yhao.floatwindow.utils.L;
import com.yhao.floatwindow.utils.ViewUtils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description:
 * @Version: 1.0.9
 * @Create: 2017/12/29 17:15:35
 * @Author: yhao
 * @Modify: sanbo
 */
public class IFloatWindowImpl extends BaseFloatWindow {

    private FloatWindow.Builder mBuilder;
    private BaseFloatView mFloatView;
    private FloatLifecycleReceiver mFloatLifecycle;
    private boolean isShow;
    private boolean once = true;
    private ValueAnimator mAnimator;
    private TimeInterpolator mDecelerateInterpolator;
    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private boolean mClick = false;
    private int mSlop;
    private int screenWidth, screenHeight;

    @SuppressWarnings("unused")
    private IFloatWindowImpl() {}

    public IFloatWindowImpl(FloatWindow.Builder b) {
        if (b == null) {
            return;
        }
        mBuilder = b;
        checkScreenSize();

        if (mBuilder.mMoveType == MoveType.FIXED) {
            if (Build.VERSION.SDK_INT >= 25) {
                mFloatView = new FloatPhone(b.mApplicationContext, mBuilder.mPermissionListener);
            } else {
                mFloatView = new FloatToast(b.mApplicationContext);
            }
        } else {
            mFloatView = new FloatPhone(b.mApplicationContext, mBuilder.mPermissionListener);
            initTouchEvent();
        }
        mFloatView.setSize(mBuilder.mWidth, mBuilder.mHeight);
        mFloatView.setGravity(mBuilder.gravity, mBuilder.xOffset, mBuilder.yOffset);
        mFloatView.setView(mBuilder.mView);
        mFloatLifecycle = new FloatLifecycleReceiver(mBuilder.mApplicationContext, mBuilder.mShow, mBuilder.mActivities,
            new LifecycleListener() {
                // new FloatLifecycleReceiver(mBuilder.mApplicationContext, mBuilder.mShow, mBuilder.mActivities,
                // new LifecycleListener() {
                @Override
                public void onShow() {
                    // show();
                }

                @Override
                public void onHide() {
                    // hide();
                }

                @Override
                public void onBackToDesktop() {
                    if (!mBuilder.mDesktopShow) {
                        hide();
                    }
                    if (mBuilder.mViewStateListener != null) {
                        mBuilder.mViewStateListener.onBackToDesktop();
                    }
                }
            });
    }

    @Override
    public void show() {
        if (once) {
            mFloatView.init();
            once = false;
            isShow = true;
        } else {
            if (isShow) {
                return;
            }
            getView().setVisibility(View.VISIBLE);
            isShow = true;
        }
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onShow();
        }
    }

    @Override
    public void hide() {
        if (once || !isShow) {
            return;
        }
        getView().setVisibility(View.INVISIBLE);
        isShow = false;
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onHide();
        }
    }

    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    public void dismiss() {
        mFloatView.dismiss();
        isShow = false;
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onDismiss();
        }
    }

    @Override
    public void destory() {
        if (mFloatLifecycle != null) {
            mFloatLifecycle.unRegisterReceiver(mBuilder.mApplicationContext);

        }
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    @Override
    public void updateX(int x) {
        checkMoveType();
        mBuilder.xOffset = x;
        mFloatView.updateX(x);
    }

    @Override
    public void updateY(int y) {
        checkMoveType();
        mBuilder.yOffset = y;
        mFloatView.updateY(y);
    }

    @Override
    public void updateX(Screen screenType, float ratio) {
        checkMoveType();

        // mBuilder.xOffset = (int)((screenType == Screen.WIDTH ? ViewUtils.getScreenWidth(mBuilder.mApplicationContext)
        // : ViewUtils.getScreenHeight(mBuilder.mApplicationContext)) * ratio);
        mBuilder.xOffset = (int)((screenType == Screen.WIDTH ? screenWidth : screenHeight) * ratio);
        mFloatView.updateX(mBuilder.xOffset);

    }

    @Override
    public void updateY(Screen screenType, float ratio) {
        checkMoveType();
        // mBuilder.yOffset = (int)((screenType == Screen.WIDTH ? ViewUtils.getScreenWidth(mBuilder.mApplicationContext)
        // : ViewUtils.getScreenHeight(mBuilder.mApplicationContext)) * ratio);
        mBuilder.yOffset = (int)((screenType == Screen.WIDTH ? screenWidth : screenHeight) * ratio);
        mFloatView.updateY(mBuilder.yOffset);

    }

    @Override
    public int getX() {
        return mFloatView.getX();
    }

    @Override
    public int getY() {
        return mFloatView.getY();
    }

    @Override
    public View getView() {
        mSlop = ViewConfiguration.get(mBuilder.mApplicationContext).getScaledTouchSlop();
        return mBuilder.mView;
    }

    private void checkMoveType() {
        if (mBuilder.mMoveType == MoveType.FIXED) {
            throw new IllegalArgumentException("FloatWindow of this tag is not allowed to move!");
        }
    }

    private void initTouchEvent() {
        checkScreenSize();
        if (mBuilder.mMoveType != MoveType.INACTIVE) {
            getView().setOnTouchListener(new View.OnTouchListener() {
                float lastX, lastY, changeX, changeY;
                int newX, newY;

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    try {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                onActionDown(event);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                onActionMove(event);
                                break;
                            case MotionEvent.ACTION_UP:
                                onActionUp(event, v);
                                break;
                            default:
                                break;
                        }
                    } catch (Throwable e) {
                        L.e(e);
                    }
                    return mClick;
                }

                private void onActionDown(MotionEvent event) {
                    downX = event.getRawX();
                    downY = event.getRawY();
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    cancelAnimator();
                }

                private void onActionMove(MotionEvent event) {
                    // if (!isOutOfRange(event.getRawX(), event.getRawY())) {
                    changeX = event.getRawX() - lastX;
                    changeY = event.getRawY() - lastY;
                    newX = (int)(mFloatView.getX() + changeX);
                    newY = (int)(mFloatView.getY() + changeY);
                    mFloatView.updateXY(newX, newY);
                    if (mBuilder.mViewStateListener != null) {
                        mBuilder.mViewStateListener.onPositionUpdate(newX, newY);
                    }
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    // }
                }

                private void onActionUp(MotionEvent event, View v) {
                    upX = event.getRawX();
                    upY = event.getRawY();
                    mClick = (Math.abs(upX - downX) > mSlop) || (Math.abs(upY - downY) > mSlop);
                    // L.i("Raw [%f x %f] -- FloatView[%d x %d] 分辨率(%d---%d) ", upX, upY, mFloatView.getX(),
                    // mFloatView.getY(), screenWidth, screenHeight);
                    if (mBuilder.mMoveType == MoveType.SLIDE) {
                        int startX = mFloatView.getX();
                        int startY = mFloatView.getY();
                        // 如果图标滑动的坐标是在哪部分，根据坐标和边缘距离计算，相应的坐标
                        int endX = (startX * 2 + v.getWidth() > screenWidth)
                            ? screenWidth - v.getWidth() - mBuilder.mSlideRightMargin : mBuilder.mSlideLeftMargin;
                        // 是否需要校正Y坐标
                        if (isNeedMoveY(startY, upY)) {
                            int endY = getY(startY, upY, v.getHeight());
                            PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("x", mFloatView.getX(), endX);
                            PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("y", mFloatView.getY(), endY);
                            mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY);
                            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int x = (Integer)animation.getAnimatedValue("x");
                                    int y = (Integer)animation.getAnimatedValue("y");
                                    mFloatView.updateXY(x, y);
                                    if (mBuilder.mViewStateListener != null) {
                                        mBuilder.mViewStateListener.onPositionUpdate(x, y);
                                    }
                                }
                            });
                        } else {
                            mAnimator = ObjectAnimator.ofInt(startX, endX);
                            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int x = (Integer)animation.getAnimatedValue();
                                    mFloatView.updateX(x);
                                    if (mBuilder.mViewStateListener != null) {
                                        mBuilder.mViewStateListener.onPositionUpdate(x, (int)upY);
                                    }
                                }
                            });
                        }
                        startAnimator();
                    } else if (mBuilder.mMoveType == MoveType.BACK) {
                        PropertyValuesHolder pvhX =
                            PropertyValuesHolder.ofInt("x", mFloatView.getX(), mBuilder.xOffset);
                        PropertyValuesHolder pvhY =
                            PropertyValuesHolder.ofInt("y", mFloatView.getY(), mBuilder.yOffset);
                        mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY);
                        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int x = (Integer)animation.getAnimatedValue("x");
                                int y = (Integer)animation.getAnimatedValue("y");
                                mFloatView.updateXY(x, y);
                                if (mBuilder.mViewStateListener != null) {
                                    mBuilder.mViewStateListener.onPositionUpdate(x, y);
                                }
                            }
                        });
                        startAnimator();
                    }
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                }

                /**
                 * 是否需要移动Y坐标
                 * 
                 * @param startY 悬浮窗获取的位置
                 * @param upY 真实针对屏幕的位置
                 * @return
                 */
                private boolean isNeedMoveY(int startY, float upY) {
                    if (startY < 0 || upY > screenHeight) {
                        return true;
                    }
                    return false;
                }

                /**
                 * 获取Y的坐标
                 * 
                 * @param startY 悬浮窗获取的位置
                 * @param upY 真实针对屏幕的位置
                 * @param height 组建高度
                 * @return
                 */
                private int getY(int startY, float upY, int height) {
                    // 挨近顶部处理
                    if (startY < 0) {
                        // return (int)(upY + (0 - startY));
                        // return height;
                        return (int)Math.abs(startY - upY);
                    }
                    // 接近底部处理
                    if (upY > screenHeight) {
                        return (int)(screenHeight - height - (Math.abs(screenHeight - upY)));
                    }
                    return 0;
                }
            });
        }
    }

    /**
     * 初始化屏幕分辨率
     */
    private void checkScreenSize() {
        if (screenWidth == 0) {
            screenWidth = ViewUtils.getScreenWidth(mBuilder.mApplicationContext);
        }
        if (screenHeight == 0) {
            screenHeight = ViewUtils.getScreenHeight(mBuilder.mApplicationContext);
        }
    }

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    // /**
    // * 判断是否超出范围，根据自己需求设置比例大小，我自己设置的是0.025和0.975
    // * 这是合并一个哥们 没有完善。暂时不使用
    // * https://github.com/yhaolpz/FloatWindow/pull/89/commits/f48b0ea10351246da43bf7e259855a91e1314dbc
    // * @param x event.getRawX()
    // * @param y event.getRawY()
    // * @return
    // */
    // private boolean isOutOfRange(float x, float y) {
    // boolean b = true;
    // // float screenWidth = ViewUtils.getScreenWidth(mBuilder.mApplicationContext);
    // // float screenHeight = ViewUtils.getScreenHeight(mBuilder.mApplicationContext);
    // float widthRate, heightRate;
    // widthRate = (screenWidth - x) / screenWidth;
    // heightRate = (screenHeight - y) / screenHeight;
    // if (widthRate > 0.025 && widthRate < 0.975 && heightRate > 0.025 && heightRate < 0.975) {
    // b = false;
    // } else {
    // b = true;
    // }
    // return b;
    // }

    private void startAnimator() {
        if (mBuilder.mInterpolator == null) {
            if (mDecelerateInterpolator == null) {
                mDecelerateInterpolator = new DecelerateInterpolator();
            }
            mBuilder.mInterpolator = mDecelerateInterpolator;
        }
        mAnimator.setInterpolator(mBuilder.mInterpolator);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.removeAllListeners();
                mAnimator = null;
                if (mBuilder.mViewStateListener != null) {
                    mBuilder.mViewStateListener.onMoveAnimEnd();
                }
            }
        });
        mAnimator.setDuration(mBuilder.mDuration).start();
        if (mBuilder.mViewStateListener != null) {
            mBuilder.mViewStateListener.onMoveAnimStart();
        }
    }

}
