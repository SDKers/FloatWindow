package com.yhao.floatwindow.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Copyright © 2017 Analysys Inc. All rights reserved.
 * @Description: TODO
 * @Version: 1.0.9
 * @Create: 2017-12/23 17:15:35
 * @Author: yhao
 */
public class Screen {
    public static final int WIDTH = 0;
    public static final int HEIGHT = 1;

    @IntDef({WIDTH, HEIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface screenType {}
}
