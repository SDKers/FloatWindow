package com.yhao.floatwindow.annotation;

/**
 * @Copyright © 2019 Analysys Inc. All rights reserved.
 * @Description: 移动类型,去除V4依赖
 *               </p>
 *               SLIDE : 可拖动，释放后自动贴边 （默认）
 *               </p>
 *               BACK : 可拖动，释放后自动回到原位置
 *               </p>
 *               ACTIVE : 可拖动
 *               </p>
 *               INACTIVE : 不可拖动
 * @Version: 1.0
 * @Create: Feb 19, 2019 11:32:21 AM
 * @Author: sanbo
 */
public enum MoveType {
    FIXED, INACTIVE, ACTIVE, SLIDE, BACK
}