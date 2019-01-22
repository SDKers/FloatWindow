package com.yhao.floatwindow;

import java.util.HashMap;
import java.util.Map;

import com.yhao.floatwindow.annotation.MoveType;
import com.yhao.floatwindow.annotation.Screen;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yhao on 2017/12/22. https://github.com/yhaolpz
 */

public class FloatWindow {

    private FloatWindow() {

    }

    private static final String mDefaultTag = "default_float_window_tag";
    private static Map<String, IFloatWindow> mFloatWindowMap;

    public static IFloatWindow get() {
        return get(mDefaultTag);
    }

    public static IFloatWindow get(String tag) {
        return mFloatWindowMap == null ? null : mFloatWindowMap.get(tag);
    }

    private static Builder mBuilder = null;

    public static Builder with(Context applicationContext) {
        return mBuilder = new Builder(applicationContext);
    }

    public static void destroy() {
        destroy(mDefaultTag);
    }

    public static void destroy(String tag) {
        if (mFloatWindowMap == null || !mFloatWindowMap.containsKey(tag)) {
            return;
        }
        mFloatWindowMap.get(tag).dismiss();
        mFloatWindowMap.remove(tag);
    }

    public static class Builder {
        Context mApplicationContext;
        View mView;
        private int mLayoutId;
        int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        int gravity = Gravity.TOP | Gravity.START;
        int xOffset;
        int yOffset;
        boolean mShow = true;
        Class[] mActivities;
        int mMoveType = MoveType.slide;
        int mSlideLeftMargin;
        int mSlideRightMargin;
        long mDuration = 300;
        TimeInterpolator mInterpolator;
        private String mTag = mDefaultTag;
        boolean mDesktopShow;
        PermissionListener mPermissionListener;
        ViewStateListener mViewStateListener;

        private Builder() {

        }

        Builder(Context applicationContext) {
            mApplicationContext = applicationContext;
        }

        public Builder setView(View view) {
            mView = view;
            return this;
        }

        public Builder setView(int layoutId) {
            mLayoutId = layoutId;
            return this;
        }

        public Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public Builder setHeight(int height) {
            mHeight = height;
            return this;
        }

        public Builder setWidth(@Screen.screenType int screenType, float ratio) {
            mWidth = (int)((screenType == Screen.width ? Util.getScreenWidth(mApplicationContext)
                : Util.getScreenHeight(mApplicationContext)) * ratio);
            return this;
        }

        public Builder setHeight(@Screen.screenType int screenType, float ratio) {
            mHeight = (int)((screenType == Screen.width ? Util.getScreenWidth(mApplicationContext)
                : Util.getScreenHeight(mApplicationContext)) * ratio);
            return this;
        }

        public Builder setX(int x) {
            xOffset = x;
            return this;
        }

        public Builder setY(int y) {
            yOffset = y;
            return this;
        }

        public Builder setX(@Screen.screenType int screenType, float ratio) {
            xOffset = (int)((screenType == Screen.width ? Util.getScreenWidth(mApplicationContext)
                : Util.getScreenHeight(mApplicationContext)) * ratio);
            return this;
        }

        public Builder setY(@Screen.screenType int screenType, float ratio) {
            yOffset = (int)((screenType == Screen.width ? Util.getScreenWidth(mApplicationContext)
                : Util.getScreenHeight(mApplicationContext)) * ratio);
            return this;
        }

        /**
         * 设置 Activity 过滤器，用于指定在哪些界面显示悬浮窗，默认全部界面都显示
         *
         * @param show 过滤类型,子类类型也会生效
         * @param activities 过滤界面
         */
        public Builder setFilter(boolean show, Class... activities) {
            mShow = show;
            mActivities = activities;
            return this;
        }

        public Builder setMoveType(@MoveType.MOVE_TYPE int moveType) {
            return setMoveType(moveType, 0, 0);
        }

        /**
         * 设置带边距的贴边动画，只有 moveType 为 MoveType.slide，设置边距才有意义，这个方法不标准，后面调整
         *
         * @param moveType 贴边动画 MoveType.slide
         * @param slideLeftMargin 贴边动画左边距，默认为 0
         * @param slideRightMargin 贴边动画右边距，默认为 0
         */
        public Builder setMoveType(@MoveType.MOVE_TYPE int moveType, int slideLeftMargin, int slideRightMargin) {
            mMoveType = moveType;
            mSlideLeftMargin = slideLeftMargin;
            mSlideRightMargin = slideRightMargin;
            return this;
        }

        public Builder setMoveStyle(long duration, TimeInterpolator interpolator) {
            mDuration = duration;
            mInterpolator = interpolator;
            return this;
        }

        public Builder setTag(String tag) {
            mTag = tag;
            return this;
        }

        public Builder setDesktopShow(boolean show) {
            mDesktopShow = show;
            return this;
        }

        public Builder setPermissionListener(PermissionListener listener) {
            mPermissionListener = listener;
            return this;
        }

        public Builder setViewStateListener(ViewStateListener listener) {
            mViewStateListener = listener;
            return this;
        }

        public void build() {
            if (mFloatWindowMap == null) {
                mFloatWindowMap = new HashMap<String, IFloatWindow>();
            }
            if (mFloatWindowMap.containsKey(mTag)) {
                throw new IllegalArgumentException(
                    "FloatWindow of this tag has been added, Please set a new tag for the new FloatWindow");
            }
            if (mView == null && mLayoutId == 0) {
                throw new IllegalArgumentException("View has not been set!");
            }
            if (mView == null) {
                mView = Util.inflate(mApplicationContext, mLayoutId);
            }
            IFloatWindow floatWindowImpl = new IFloatWindowImpl(this);
            mFloatWindowMap.put(mTag, floatWindowImpl);
        }

    }
}
