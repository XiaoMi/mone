package com.xiaomi.youpin.tesla.ip.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @author goodjava@qq.com
 * @date 2023/5/19 22:25
 */
public class ProxyUtils {

    public static String getProxy() {
        String proxy = ConfigUtils.getConfig().getChatgptProxy();
        if (StringUtils.isEmpty(proxy)) {
            return null;
        }
        return proxy;
    }

}
