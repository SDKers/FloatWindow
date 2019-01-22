package com.yhao.floatwindow.impl;

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

import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.annotation.MoveType;
import com.yhao.floatwindow.annotation.Screen;
import com.yhao.floatwindow.interfaces.BaseFloatView;
import com.yhao.floatwindow.interfaces.BaseFloatWindow;
import com.yhao.floatwindow.interfaces.LifecycleListener;
import com.yhao.floatwindow.utils.Util;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description:
 * @Version: 1.0.9
 * @Create: 2017/12/29 17:15:35
 * @Author: yhao
 */
public class IFloatWindowImpl extends BaseFloatWindow {

    private FloatWindow.Builder mBuilder;
    private BaseFloatView mFloatView;
    // private FloatLifecycleReceiver mFloatLifecycle;
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

    @SuppressWarnings("unused")
    private IFloatWindowImpl() {}

    public IFloatWindowImpl(FloatWindow.Builder b) {
        mBuilder = b;
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
        // mFloatLifecycle = new FloatLifecycleReceiver(mBuilder.mApplicationContext, mBuilder.mShow,
        // mBuilder.mActivities, new
        // LifecycleListener() {
        new FloatLifecycleReceiver(mBuilder.mApplicationContext, mBuilder.mShow, mBuilder.mActivities,
            new LifecycleListener() {
                @Override
                public void onShow() {
                    show();
                }

                @Override
                public void onHide() {
                    hide();
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
    public void updateX(int screenType, float ratio) {
        checkMoveType();
        mBuilder.xOffset = (int)((screenType == Screen.WIDTH ? Util.getScreenWidth(mBuilder.mApplicationContext)
            : Util.getScreenHeight(mBuilder.mApplicationContext)) * ratio);
        mFloatView.updateX(mBuilder.xOffset);

    }

    @Override
    public void updateY(int screenType, float ratio) {
        checkMoveType();
        mBuilder.yOffset = (int)((screenType == Screen.WIDTH ? Util.getScreenWidth(mBuilder.mApplicationContext)
            : Util.getScreenHeight(mBuilder.mApplicationContext)) * ratio);
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
        switch (mBuilder.mMoveType) {
            case MoveType.INACTIVE:
                break;
            default:
                getView().setOnTouchListener(new View.OnTouchListener() {
                    float lastX, lastY, changeX, changeY;
                    int newX, newY;

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                downX = event.getRawX();
                                downY = event.getRawY();
                                lastX = event.getRawX();
                                lastY = event.getRawY();
                                cancelAnimator();
                                break;
                            case MotionEvent.ACTION_MOVE:
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
                                break;
                            case MotionEvent.ACTION_UP:
                                upX = event.getRawX();
                                upY = event.getRawY();
                                mClick = (Math.abs(upX - downX) > mSlop) || (Math.abs(upY - downY) > mSlop);
                                switch (mBuilder.mMoveType) {
                                    case MoveType.SLIDE:
                                        int startX = mFloatView.getX();
                                        int endX = (startX * 2 + v.getWidth() > Util
                                            .getScreenWidth(mBuilder.mApplicationContext))
                                                ? Util.getScreenWidth(mBuilder.mApplicationContext) - v.getWidth()
                                                    - mBuilder.mSlideRightMargin
                                                : mBuilder.mSlideLeftMargin;
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
                                        startAnimator();
                                        break;
                                    case MoveType.BACK:
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
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            default:
                                break;
                        }
                        return mClick;
                    }
                });
        }
    }

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

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

}
