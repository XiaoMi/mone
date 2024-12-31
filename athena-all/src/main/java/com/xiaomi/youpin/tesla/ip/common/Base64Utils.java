package com.xiaomi.youpin.tesla.ip.common;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * @author goodjava@qq.com
 * @date 2024/6/19 08:47
 */
public class Base64Utils {

    public static String decodeBase64String(String str) {
        return new String(Base64.getDecoder().decode(str.getBytes(Charset.forName("utf8"))), Charset.forName("utf8"));
    }

}
