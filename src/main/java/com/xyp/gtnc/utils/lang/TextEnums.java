package com.xyp.gtnc.utils.lang;

import org.jetbrains.annotations.ApiStatus;

import com.xyp.gtnc.utils.Utils;

public enum TextEnums {

    // #tr Word_Parallel
    // # Parallel
    // #zh_CN 并行
    Word_Parallel("Word_Parallel");

    @ApiStatus.Obsolete
    public static String tr(String key) {
        return Utils.tr(key);
    }

    @ApiStatus.Obsolete
    public static String tr(String key, Object... format) {
        return Utils.tr(key, format);
    }

    private final String text;
    private final String key;

    TextEnums(String key) {
        this.key = key;
        this.text = tr(key);
    }

    @Override
    public String toString() {
        return text;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

}
