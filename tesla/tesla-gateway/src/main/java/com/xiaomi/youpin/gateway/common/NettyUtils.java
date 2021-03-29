package com.xiaomi.youpin.gateway.common;

import io.netty.util.ReferenceCounted;
import lombok.extern.slf4j.Slf4j;

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

}
