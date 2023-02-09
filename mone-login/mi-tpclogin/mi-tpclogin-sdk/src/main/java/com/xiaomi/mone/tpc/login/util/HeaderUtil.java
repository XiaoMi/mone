package com.xiaomi.mone.tpc.login.util;

import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:33
 */
public class HeaderUtil {

    private static final ThreadLocal<Map<String, String>> local = new ThreadLocal();

    public static void addHeader(String key, String val) {
        Map<String, String> headers = local.get();
        if (headers == null) {
            headers = new HashMap<>();
            local.set(headers);
        }
        headers.put(key.toLowerCase(), val);
    }

    public static String getHeader(String key) {
        Map<String, String> headers = local.get();
        if (headers == null) {
            return null;
        }
        return headers.get(key.toLowerCase());
    }


    public static void delHeader(String key) {
        Map<String, String> headers = local.get();
        if (headers == null) {
            return;
        }
        headers.remove(key.toLowerCase());
    }

    public static void clearHeader() {
        local.remove();
    }

}
