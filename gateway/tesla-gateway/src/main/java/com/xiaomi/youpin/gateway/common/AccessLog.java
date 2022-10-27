package com.xiaomi.youpin.gateway.common;

import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import static com.xiaomi.youpin.gateway.common.Const.*;
import static com.xiaomi.youpin.gateway.common.Const.MDC_PATH;
import static com.xiaomi.youpin.gateway.common.NettyUtils.remoteIpFromRequest;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/3/16
 *
 * Attention: logger file在logback被配到了access.log里
 */
@Slf4j
public class AccessLog {

    public static void logAccess(FullHttpRequest request, FullHttpResponse response, long startTs, ApiInfo apiInfo, FilterContext context) {
        MDC.put(MDC_REMOTE, remoteIpFromRequest(request));
        MDC.put(MDC_URI, request.uri());
        MDC.put(MDC_DELAY, String.valueOf(System.currentTimeMillis() - startTs));

        if (response != null) {
            MDC.put(MDC_CODE, String.valueOf(response.status().code()));
        }
        if (apiInfo != null) {
            MDC.put(MDC_GATEWAY_ID, String.valueOf(apiInfo.getId()));
            MDC.put(MDC_APPLICATION, apiInfo.getApplication());
            MDC.put(MDC_PATH, apiInfo.getPath());
        }
        log.info("{} {} >> {}, flag: {}, type: {}, rpc: {}ms", request.protocolVersion().toString(), request.method().name(),
                request.headers().get("User-Agent"), apiInfo == null ? "null" : apiInfo.getFlag(),
                apiInfo == null ? "null" : apiInfo.getRouteType(), context.getRpcUseTime());

        MDC.remove(MDC_REMOTE);
        MDC.remove(MDC_URI);
        MDC.remove(MDC_DELAY);

        if (response != null) {
            MDC.remove(MDC_CODE);
        }
        if (apiInfo != null) {
            MDC.remove(MDC_GATEWAY_ID);
            MDC.remove(MDC_APPLICATION);
            MDC.remove(MDC_PATH);
        }
    }
}
