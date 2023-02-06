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

import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.gateway.common.ByteBufUtils;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpRequestUtils;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.protocol.http.HttpClient;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.gateway.sidecar.FilterHttpData;
import com.xiaomi.youpin.gateway.sidecar.FilterRequest;
import com.xiaomi.youpin.gateway.sidecar.FilterResponse;
import com.xiaomi.youpin.gateway.sidecar.SidecarService;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 * @date 2022/6/18
 *
 * 主要用来测试代码
 */
@Slf4j
//@Component
@FilterOrder(Integer.MIN_VALUE + 1000)
public class SidecarFilter extends RequestFilter {

    private final String URL = "/mtop/arch/ping";

    @Autowired
    private ConfigService configService;

    @Autowired
    private SidecarService sidecarService;

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (apiInfo.getUrl().equals(URL)) {
            UdsCommand udsCommand = UdsCommand.createRequest();
            udsCommand.setApp("demoApp");
            udsCommand.setCmd("execute");
            FilterHttpData httpData = new FilterHttpData();
            httpData.setApiInfo(apiInfo);
            httpData.setFilterContext(context);
            FilterRequest filterRequest = new FilterRequest();
            filterRequest.setMethod(request.method().name());
            filterRequest.setQueryString(HttpRequestUtils.getQueryString(request));
            if (filterRequest.getMethod().equalsIgnoreCase("post")) {
                filterRequest.setBody(HttpClient.getBody(context, request));
            }
            request.headers().forEach(it -> filterRequest.getHeaders().put(it.getKey(), it.getValue()));
            httpData.setRequest(filterRequest);
            udsCommand.setData(httpData);
            FilterResponse res = sidecarService.call(udsCommand);
            ByteBuf buf = ByteBufUtils.createBuf(context, new String(res.getData()), configService.isAllowDirectBuf());
            return HttpResponseUtils.create(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf));
        }
        return invoker.doInvoker(context, apiInfo, request);
    }
}
