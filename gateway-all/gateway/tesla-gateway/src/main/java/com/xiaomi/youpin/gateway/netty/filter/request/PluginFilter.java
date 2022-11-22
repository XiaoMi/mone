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

import com.google.gson.Gson;
import com.xiaomi.youpin.gateway.RouteType;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpRequestUtils;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.common.Msg;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.plugin.TeslaPluginManager;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.plugin.bo.Request;
import com.youpin.xiaomi.tesla.plugin.bo.RequestContext;
import com.youpin.xiaomi.tesla.plugin.bo.Response;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * 以插件模式执行(打好的jar包)
 */
@Component
@FilterOrder(4000 + 1)
@Slf4j
public class PluginFilter extends RequestFilter {

    @Autowired
    private TeslaPluginManager teslaPluginManager;

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (apiInfo.getRouteType() == RouteType.Plugin.type()) {
            RequestContext requestContext = new RequestContext();
            requestContext.setRegConsumer(context.getRequestContext().getRegConsumer());
            requestContext.setSendConsumer(context.getRequestContext().getSendConsumer());
            requestContext.setGroupFunction(context.getRequestContext().getGroupFunction());
            requestContext.setUser(context.getRequestContext().getUser());
            requestContext.setPingConsumer(context.getRequestContext().getPingConsumer());

            byte[] data = HttpRequestUtils.getRequestBody(request);
            if (null == data) {
                data = new byte[]{};
            }

            String contentType = request.headers().get("Content-Type");
            if (contentType == null) {
                contentType = "";
            }

            Gson gson = new Gson();
            try {
                Request req = new Request();
                if (contentType.toLowerCase().contains("x-www-form-urlencoded")) {
                    String content = new String(data);
                    String[] pairs = content.split("\\&");
                    for (int i = 0; i < pairs.length; i++) {
                        String[] fields = pairs[i].split("=");
                        if (fields.length >= 2) {
                            String name = URLDecoder.decode(fields[0], "UTF-8");
                            String value = URLDecoder.decode(fields[1], "UTF-8");
                            req.put(name, value);
                        }
                    }
                } else {
                    req = gson.fromJson(new String(data), Request.class);
                    if (req == null) {
                        req = new Request();
                    }
                }

                if (StringUtils.isNotEmpty(context.getUid())) {
                    req.put("uid", context.getUid());
                }
                if (StringUtils.isNotEmpty(context.getIp())) {
                    req.put("ip", context.getIp());
                }

                for (Map.Entry<String, String> entry : request.headers().entries()) {
                    req.put("gwh:" + entry.getKey(), entry.getValue());
                }

                QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
                Map<String, List<String>> queryMap = decoder.parameters();

                //support get 参数提取
                for (Map.Entry<String, List<String>> entry : queryMap.entrySet()) {
                    if (entry.getValue().size() == 1) {
                        req.put("gwg:" + entry.getKey(), entry.getValue().get(0));
                    }
                }

                Response<String> res = teslaPluginManager.call(apiInfo.getUrl(), requestContext, req);

                //需要跳转
                if (res.getCode() == GeneralCodes.FOUND.getCode()) {
                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
                    response.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
                    response.headers().set(HttpHeaderNames.LOCATION, res.getData());
                    res.getHeaders().entrySet().stream().forEach(it -> response.headers().set(it.getKey(), it.getValue()));
                    HttpUtil.setKeepAlive(response, true);
                    return response;
                }

                return HttpResponseUtils.create(new Gson().toJson(res), res.getHeaders());
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
                return HttpResponseUtils.create(Result.fail(GeneralCodes.InternalError, Msg.msgFor500, ex.getMessage()));
            }
        }

        return invoker.doInvoker(context, apiInfo, request);
    }
}
