package com.xiaomi.mone.tpc.login.util;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/8 16:47
 */
public class CommonUtil {

    /**
     * 检测是否为忽略路径
     */
    public static boolean isIgnoreUrl(String[] ignoreUrlArr, String url) {
        if (ignoreUrlArr == null || ignoreUrlArr.length < 1) {
            return false;
        }
        for (String ignoreUrl : ignoreUrlArr) {
            if (url.equals(ignoreUrl)) {
                return true;
            }
            if (ignoreUrl.endsWith("*")) {
                boolean sign = url.startsWith(ignoreUrl.substring(0, ignoreUrl.length() - 1));
                if (sign) {
                    return true;
                }
            }
        }
        return false;
    }
}
