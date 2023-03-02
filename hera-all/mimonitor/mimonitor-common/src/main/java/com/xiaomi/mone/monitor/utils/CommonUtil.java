/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor.utils;

/**
 *
 * @author zhanggaofeng1
 */
public class CommonUtil {

    public static Long toSeconds(Long millis) {
        if (millis == null || millis <= 0L) {
            return 0L;
        }
        return millis / 1000L;
    }

    public static Long toMillis(Long seconds) {
        if (seconds == null || seconds <= 0L) {
            return 0L;
        }
        return seconds * 1000L;
    }

}
