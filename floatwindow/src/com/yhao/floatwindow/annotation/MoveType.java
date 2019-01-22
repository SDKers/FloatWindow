package com.yhao.floatwindow.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description: TODO
 * @Version: 1.0.9
 * @Create: 2017-12/22 17:15:35
 * @Author: yhao
 */
public class MoveType {
    public static final int FIXED = 0;
    public static final int INACTIVE = 1;
    public static final int ACTIVE = 2;
    public static final int SLIDE = 3;
    public static final int BACK = 4;

    @IntDef({FIXED, INACTIVE, ACTIVE, SLIDE, BACK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MOVE_TYPE {}
}
