package com.xiaomi.mone.monitor.service.alertmanager.client.util;

import java.util.Locale;

/**
 * @author gaoxihui
 * @date 2022/11/10 3:12 下午
 */
public class BinaryUtils {
    public BinaryUtils() {
    }

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        byte[] bytes = data;
        int len = data.length;

        for(int i = 0; i < len; ++i) {
            byte b = bytes[i];
            String hex = Integer.toHexString(b);
            if (hex.length() == 1) {
                sb.append("0");
            } else if (hex.length() == 8) {
                hex = hex.substring(6);
            }

            sb.append(hex);
        }

        return sb.toString().toLowerCase(Locale.getDefault());
    }
}
