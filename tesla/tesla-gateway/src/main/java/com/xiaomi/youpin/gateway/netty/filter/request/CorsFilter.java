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
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.handler.codec.http.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 设置允许跨域
 *
 * @author libaokang
 * @date 2019-03-26
 */
@Component
@FilterOrder(Integer.MIN_VALUE + 12)
public class CorsFilter extends RequestFilter {


    private static final String OPTIONS = "options";

    private static final List<String> ALLOW_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {


        if (apiInfo.isAllow(Flag.ALLOW_CORS)) {
            if (request.method().name().toLowerCase().equals(OPTIONS)) {
                FullHttpResponse response = HttpResponseUtils.createDefaultSuccess();
                setHeaders(response);
                return response;
            }

            FullHttpResponse response = invoker.doInvoker(context, apiInfo, request);
            setHeaders(response);
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION, "close");
            HttpUtil.setKeepAlive(response, false);
            return response;
        }

        return invoker.doInvoker(context, apiInfo, request);


    }

    private void setHeaders(HttpResponse response) {
        HttpHeaders headers = response.headers();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Credentials", "true");
        headers.set("Access-Control-Allow-Methods", ALLOW_METHODS);
        headers.set("Access-Control-Allow-Headers", "X-Yp-App-Source, Content-Type, Content-Length, Authorization, Accept, X-Requested-With");
    }


}
