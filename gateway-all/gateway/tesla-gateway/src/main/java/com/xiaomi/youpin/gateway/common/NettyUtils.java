package com.xiaomi.youpin.gateway.common;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCounted;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class NettyUtils {


    public static void release(Object msg, String message) {
        if (null == msg) {
            return;
        }
        if (msg instanceof ReferenceCounted) {
            int count = ((ReferenceCounted) msg).refCnt();
            if (count > 1) {
                log.warn("release count>1:{}{}", count, message);
            }
            if (count == 0) {
                return;
            }
            ((ReferenceCounted) msg).release(count);
        }
    }

    public static String remoteIpFromRequest(FullHttpRequest request) {
        String ip = request.headers().get("X-Forwarded-For");

        if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.headers().get("Proxy-Client-IP");
        }
        if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.headers().get("WL-Proxy-Client-IP");
        }
        if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.headers().get("HTTP_CLIENT_IP");
        }
        if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.headers().get("HTTP_X_FORWARDED_FOR");
        }
        return ip;
    }
}
