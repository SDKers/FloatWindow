package com.yhao.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */
public class IFloatWindowImpl extends IFloatWindow {


    private FloatWindow.Builder mBuilder;
    private FloatView mFloatView;
    private FloatLifecycle mFloatLifecycle;
    private boolean isShow;
    private boolean once = true;
    private ValueAnimator mAnimator;
    private TimeInterpolator mDecelerateInterpolator;

    private IFloatWindowImpl() {

    }

    IFloatWindowImpl(FloatWindow.Builder builder) {
        mBuilder = builder;
        if (mBuilder.mMoveType == MoveType.fixed) {
            if (Build.VERSION.SDK_INT >= 25) {
                mFloatView = new FloatPhone(builder.mApplicationContext);
            } else {
                mFloatView = new FloatToast(builder.mApplicationContext);
            }
        } else {
            mFloatView = new FloatPhone(builder.mApplicationContext);
            initTouchEvent();
        }
        mFloatView.setSize(mBuilder.mWidth, mBuilder.mHeight);
        mFloatView.setGravity(mBuilder.gravity, mBuilder.xOffset, mBuilder.yOffset);
        mFloatView.setView(mBuilder.mView);
        mFloatLifecycle = new FloatLifecycle(mBuilder.mApplicationContext, mBuilder.mShow, mBuilder.mActivities, new LifecycleListener() {
            @Override
            public void onShow() {
                //注释,此处可以控制build是否不直接展示
//                show();
            }

            @Override
            public void onHide() {
                //注释,此处可以控制build是否不直接展示
//                hide();
            }

            @Override
            public void onBackToDesktop() {
                if (!mBuilder.mDesktopShow) {
                    hide();
                }
            }
        });
    }

    @Override
    public void show() {
        LogUtil.i("show onece:" + once + "----isSHOW:" + isShow);
        if (once) {
            mFloatView.init();
            once = false;
            isShow = true;
        } else {
            if (isShow) return;
            getView().setVisibility(View.VISIBLE);
            isShow = true;
        }
    }

    @Override
    public void hide() {
        if (once || !isShow) return;
        getView().setVisibility(View.INVISIBLE);
        isShow = false;
    }

    @Override
    void dismiss() {
        isShow = false;
        once = true;
        if (mFloatView != null) {
            mFloatView.dismiss();
        }
        mBuilder = null;
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
        mBuilder.xOffset = (int) ((screenType == Screen.width ?
                Util.getScreenWidth(mBuilder.mApplicationContext) :
                Util.getScreenHeight(mBuilder.mApplicationContext)) * ratio);
        mFloatView.updateX(mBuilder.xOffset);

    }

    @Override
    public void updateY(int screenType, float ratio) {
        checkMoveType();
        mBuilder.yOffset = (int) ((screenType == Screen.width ?
                Util.getScreenWidth(mBuilder.mApplicationContext) :
                Util.getScreenHeight(mBuilder.mApplicationContext)) * ratio);
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
        return mBuilder.mView;
    }


    @Override
    public boolean isViewVisible() {
        return isShow;
    }


    private void checkMoveType() {
        if (mBuilder.mMoveType == MoveType.fixed) {
            LogUtil.e("FloatWindow of this tag is not allowed to move!");
        }
    }

    private void initTouchEvent() {
        switch (mBuilder.mMoveType) {
            case MoveType.inactive:
                break;
            default:
                getView().setOnTouchListener(new View.OnTouchListener() {
                    float lastX, lastY, changeX, changeY;
                    int newX, newY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                lastX = event.getRawX();
                                lastY = event.getRawY();
                                cancelAnimator();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                changeX = event.getRawX() - lastX;
                                changeY = event.getRawY() - lastY;
                                newX = (int) (mFloatView.getX() + changeX);
                                newY = (int) (mFloatView.getY() + changeY);
                                mFloatView.updateXY(newX, newY);
                                lastX = event.getRawX();
                                lastY = event.getRawY();
                                break;
                            case MotionEvent.ACTION_UP:
                                switch (mBuilder.mMoveType) {
                                    case MoveType.slide:
                                        int startX = mFloatView.getX();
                                        int endX = (startX * 2 + v.getWidth() >
                                                Util.getScreenWidth(mBuilder.mApplicationContext)) ?
                                                Util.getScreenWidth(mBuilder.mApplicationContext) - v.getWidth() : 0;
                                        mAnimator = ObjectAnimator.ofInt(startX, endX);
                                        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                int x = (int) animation.getAnimatedValue();
                                                mFloatView.updateX(x);
                                            }
                                        });
                                        startAnimator();
                                        break;
                                    case MoveType.back:
                                        PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("x", mFloatView.getX(), mBuilder.xOffset);
                                        PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("y", mFloatView.getY(), mBuilder.yOffset);
                                        mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY);
                                        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                int x = (int) animation.getAnimatedValue("x");
                                                int y = (int) animation.getAnimatedValue("y");
                                                mFloatView.updateXY(x, y);
                                            }
                                        });
                                        startAnimator();
                                        break;
                                }
                                break;

                        }
                        return false;
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
            }
        });
        mAnimator.setDuration(mBuilder.mDuration).start();
    }

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

}
