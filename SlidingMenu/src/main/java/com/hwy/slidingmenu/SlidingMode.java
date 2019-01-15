package com.hwy.slidingmenu;

/**
 * 作者: hewenyu
 * 日期: 2018/11/15 20:22
 * 说明: 侧滑的模式
 */
public enum SlidingMode {

    FROM_FULL_SCREEN(0),    // 全屏有效
    FROM_SIDE(1);   // 侧边有效

    private int mMode;

    SlidingMode(int mode) {
        this.mMode = mode;
    }

    public int getMode() {
        return this.mMode;
    }

    public static SlidingMode parseMode(int modeValue) {
        SlidingMode[] modes = SlidingMode.values();
        for (SlidingMode mode : modes) {
            if (mode.getMode() == modeValue) {
                return mode;
            }
        }
        return FROM_FULL_SCREEN;
    }

}
