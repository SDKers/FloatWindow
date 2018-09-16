package com.yhao.floatwindow;

import java.util.HashMap;
import java.util.Map;

import com.yhao.floatwindow.permission.PermissionListener;
import com.yhao.floatwindow.permission.PermissionUtil;
import com.yhao.floatwindow.utils.DeviceType;
import com.yhao.floatwindow.utils.LogUtil;
import com.yhao.floatwindow.utils.Util;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yhao on 2017/12/22. https://github.com/yhaolpz
 */
public class FloatWindow {

    private FloatWindow() {}

    public static final String DEFAULT_TAG = "default_float_window_tag";
    private static Map<String, IFloatWindow> mFloatWindowMap;
    public static Map<String, Builder> mMap = new HashMap<String, Builder>();

    public static IFloatWindow get() {
        return get(DEFAULT_TAG);
    }

    public static void prepare(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            PermissionUtil.req(context);
        } else if (Miui.rom() || DeviceType.isOppo()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionUtil.req(context);
            } else {
                Miui.req(context, new PermissionListener() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onFail() {}
                });
            }
        }
    }

    public static boolean isViewVisible(String tag) {
        IFloatWindow f = get(tag);
        if (f == null) {
            return false;
        }
        if (f.isViewVisible()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isViewVisible() {
        return isViewVisible(DEFAULT_TAG);
    }

    public static IFloatWindow get(String tag) {
        if (mMap.containsKey(tag)) {
            mMap.get(tag).build();
        }
        return mFloatWindowMap == null ? null : mFloatWindowMap.get(tag);
    }

    // private static Builder mBuilder = null;

    @MainThread
    public static Builder with(Context applicationContext) {
        // return mBuilder = new Builder(applicationContext);
        return new Builder(applicationContext);
    }

    public static void destroy() {
        destroy(DEFAULT_TAG);
    }

    public static void destroy(String tag) {
        if (mFloatWindowMap == null || !mFloatWindowMap.containsKey(tag)) {
            return;
        }
        mFloatWindowMap.get(tag).dismiss();
        mFloatWindowMap.remove(tag);
        mMap.remove(tag);
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
        Class<?>[] mActivities;
        int mMoveType = MoveType.slide;
        long mDuration = 300;
        TimeInterpolator mInterpolator;
        private String mTag = DEFAULT_TAG;
        boolean mDesktopShow;

        @SuppressWarnings("unused")
        private Builder() {}

        Builder(Context applicationContext) {
            mApplicationContext = applicationContext;
        }

        public Builder setView(View view) {
            mView = view;
            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.mView = view;
                }
            }
            return this;
        }

        public Builder setView(@LayoutRes int layoutId) {
            mLayoutId = layoutId;
            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.mLayoutId = layoutId;
                }
            }
            return this;
        }

        public Builder setWidth(int width) {
            mWidth = width;
            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.mWidth = width;
                }
            }
            return this;
        }

        public Builder setHeight(int height) {
            mHeight = height;
            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.mHeight = height;
                }
            }
            return this;
        }

        public Builder setWidth(@Screen.screenType int screenType, float ratio) {
            int width = (int)((screenType == Screen.width ? Util.getScreenWidth(mApplicationContext)
                : Util.getScreenHeight(mApplicationContext)) * ratio);
            mWidth = width;

            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.mWidth = width;
                }
            }
            return this;
        }

        public Builder setHeight(@Screen.screenType int screenType, float ratio) {

            int height = (int)((screenType == Screen.width ? Util.getScreenWidth(mApplicationContext)
                : Util.getScreenHeight(mApplicationContext)) * ratio);
            mHeight = height;

            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.mHeight = height;
                }
            }
            return this;
        }

        public Builder setX(int x) {
            xOffset = x;
            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.xOffset = x;
                }
            }
            return this;
        }

        public Builder setY(int y) {
            yOffset = y;
            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.yOffset = y;
                }
            }
            return this;
        }

        public Builder setX(@Screen.screenType int screenType, float ratio) {
            int x = (int)((screenType == Screen.width ? Util.getScreenWidth(mApplicationContext)
                : Util.getScreenHeight(mApplicationContext)) * ratio);
            xOffset = x;

            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.xOffset = x;
                }
            }
            return this;
        }

        public Builder setY(@Screen.screenType int screenType, float ratio) {
            int y = (int)((screenType == Screen.width ? Util.getScreenWidth(mApplicationContext)
                : Util.getScreenHeight(mApplicationContext)) * ratio);
            yOffset = y;

            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.yOffset = y;
                }
            }
            return this;
        }

        /**
         * 设置 Activity 过滤器，用于指定在哪些界面显示悬浮窗，默认全部界面都显示
         *
         * @param show 过滤类型,子类类型也会生效
         * @param activities 过滤界面
         */
        public Builder setFilter(boolean show, Class<?>... activities) {
            mShow = show;
            mActivities = activities;

            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.mShow = show;
                    temp.mActivities = activities;
                }
            }
            return this;
        }

        /**
         * 可拖动悬浮窗
         *
         * @param moveType
         * @return
         */
        public Builder setMoveType(@MoveType.MOVE_TYPE int moveType) {
            mMoveType = moveType;
            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.mMoveType = moveType;
                }
            }
            return this;
        }

        /**
         * 自定义动画效果，只在 MoveType.slide 或 MoveType.back 模式下设置此项才有意义。 默认减速插值器，默认动画时长为 300ms。 setMoveStyle(500, new
         * AccelerateInterpolator()) 为贴边动画时长为500ms，加速插值器
         *
         * @param duration
         * @param interpolator
         * @return
         */
        public Builder setMoveStyle(long duration, @Nullable TimeInterpolator interpolator) {
            mDuration = duration;
            mInterpolator = interpolator;
            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.mDuration = duration;
                    temp.mInterpolator = interpolator;
                }
            }
            return this;
        }

        /**
         * 唯一标志.用于控制展示和隐藏
         *
         * @param tag
         * @return
         */
        public Builder setTag(String tag) {
            mTag = tag;
            if (!mMap.containsKey(mTag)) {
                mMap.put(mTag, this);
            }
            return this;
        }

        /**
         * 桌面显示.默认false
         *
         * @param show
         * @return
         */
        public Builder setDesktopShow(boolean show) {
            mDesktopShow = show;

            if (!TextUtils.isEmpty(mTag)) {
                if (mMap.containsKey(mTag)) {
                    Builder temp = mMap.get(mTag);
                    temp.mDesktopShow = show;
                }
            }
            return this;
        }

        public void build() {
            if (mFloatWindowMap == null) {
                mFloatWindowMap = new HashMap<String, IFloatWindow>();
            }
            if (mFloatWindowMap.containsKey(mTag)) {
                LogUtil.e("已经有同名的悬浮窗");
            } else {
                if (mView == null && mLayoutId == 0) {
                    // throw new IllegalArgumentException("View has not been set!");
                    LogUtil.e("没有设置界面，请注意检查!");
                } else {
                    if (mView == null) {
                        mView = Util.inflate(mApplicationContext, mLayoutId);
                    }
                    IFloatWindow floatWindowImpl = new IFloatWindowImpl(this);
                    mFloatWindowMap.put(mTag, floatWindowImpl);
                }

            }

        }

    }
}
