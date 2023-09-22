package com.xiaomi.youpin.docean.plugin.es.antlr4.common.util;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/24 18:55
 */
public class StrUtils {

    public static final int INDEX_NOT_FOUND = -1;

    private StrUtils() {

    }

    public static String substringBetween(final String str, final String tag) {
        return substringBetween(str, tag, tag);
    }

    public static String substringBetween(final String str, final String open, final String close) {
        if (!allNotNull(str, open, close)) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            final int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    public static boolean allNotNull(final Object... values) {
        if (values == null) {
            return false;
        }

        for (final Object val : values) {
            if (val == null) {
                return false;
            }
        }

        return true;
    }
}
