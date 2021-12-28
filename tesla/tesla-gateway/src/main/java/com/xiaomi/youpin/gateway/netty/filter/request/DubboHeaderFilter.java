package com.xiaomi.youpin.gateway.netty.filter.request;

import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @Description TODO
 * @Author zhenxing.dong
 * @Date 2021/7/16 15:31
 */
@Component
@FilterOrder(1999)
public class DubboHeaderFilter extends RequestFilter {

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (apiInfo.isAllow(Flag.ALLOW_HEADER)) {
            HttpHeaders headers = request.headers();
            for (Map.Entry<String, String> entry : headers.entries()
            ) {
                RpcContext.getContext().setAttachment(entry.getKey(), entry.getValue());
            }
        }
        return invoker.doInvoker(context, apiInfo, request);
    }
}
