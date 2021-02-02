/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gateway.netty.filter.request;

import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpRequestUtils;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Component
@FilterOrder(100)
public class LogFilter extends RequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Autowired
    private ConfigService configService;


    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {

        if (configService.isCloseLogFilter()) {
            return invoker.doInvoker(context, apiInfo, request);
        }
        List<String> ids = configService.getAllowLogByUrlId();
        if (ids == null
                || ids.size() == 0
                || !ids.contains(String.valueOf(apiInfo.getId()))) {
            return invoker.doInvoker(context, apiInfo, request);
        }

        if (apiInfo.isAllow(Flag.ALLOW_LOG)) {
            long begin = System.currentTimeMillis();
            String params = "";
            if (request.method().equals(HttpMethod.GET)) {
                params = HttpRequestUtils.getQueryString(request);
            }
            if (request.method().equals(HttpMethod.POST)) {
                params = new String(HttpRequestUtils.getRequestBody(request));
            }

            String traceId = HttpRequestUtils.traceId(request);
            logger.info("invoke begin : id:{} traceId:{} method:{} uri:{} params:{} headers:{}", apiInfo.getId(), traceId, request.method(), request.uri(), params, request.headers().toString());
            try {
                FullHttpResponse res = invoker.doInvoker(context, apiInfo, request);
                String content = HttpResponseUtils.getContent(res);
                logger.info("invoke end : id:{} traceId:{} method:{} uri:{} content:{} uid:{} useTime:{}", apiInfo.getId(), traceId, request.method(), request.uri(), content, context.getUid(), System.currentTimeMillis() - begin);
                return res;
            } catch (Throwable ex) {
                //捕获下,然后打印异常
                logger.warn("invoke error : id:{} traceId:{} method:{} uri:{} params:{} ex:{} useTime:{}", apiInfo.getId(), traceId, request.method(), request.uri(), params, ex.getMessage(), System.currentTimeMillis() - begin);
                throw ex;
            }
        } else {
            return invoker.doInvoker(context, apiInfo, request);
        }
    }
}
