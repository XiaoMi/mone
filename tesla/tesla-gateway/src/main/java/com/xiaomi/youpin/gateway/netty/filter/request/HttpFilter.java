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

import com.google.common.base.Stopwatch;
import com.xiaomi.youpin.gateway.RouteType;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpRequestUtils;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.common.Utils;
import com.xiaomi.youpin.gateway.exception.GatewayException;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.protocol.http.HttpClient;
import com.xiaomi.youpin.gateway.service.GatewayNamingService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.opentelemetry.api.common.Attributes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.gateway.TeslaConstants.Tag;
import static com.xiaomi.youpin.gateway.filter.FilterContext.New_Route_Type;

/**
 * @author goodjava@qq.com
 * @author 丁海洋
 * <p>
 * Http 协议调用
 */
@Component
@FilterOrder(4000)
public class HttpFilter extends RequestFilter {

    @Autowired
    private HttpClient client;


    @Autowired
    private GatewayNamingService namingService;



    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        int routeType = apiInfo.getRouteType();
        if (StringUtils.isNotEmpty(context.getAttachments().get(New_Route_Type))) {
            routeType = Integer.valueOf(context.getAttachments().get(New_Route_Type));
        }
        if (routeType == RouteType.Http.type()) {
            try {
                adapterApiInfoPath(apiInfo, request, context);
            } catch (Throwable e) {
                return HttpResponseUtils.create(Result.fail(GeneralCodes.NotFound, HttpResponseStatus.NOT_FOUND.reasonPhrase(), e.getMessage()));
            }
            Stopwatch sw = Stopwatch.createStarted();
            try {
                return client.call(context, apiInfo, request);
            } finally {
                long useTime = sw.elapsed(TimeUnit.MILLISECONDS);
                context.setRpcUseTime(useTime);
                context.addTraceEvent("call_http_event", () -> Attributes.builder()
                        .put("time", useTime + "ms")
                        .put("timeout", apiInfo.getTimeout() + "ms")
                        .build());
            }
        }
        return invoker.doInvoker(context, apiInfo, request);
    }


    /**
     * http://www.baidu.com,http://www.163.com;http://www.sina.cn
     * 分号是用来隔开分组的(第一组production 第二组preview)
     * 支持权重的概念:http://www.baidu.com|1,http://www.163.com|2
     *
     * @param apiInfo
     * @param context
     */
    private void adapterApiInfoPath(ApiInfo apiInfo, FullHttpRequest request, FilterContext context) {
        //目标路径
        String path = apiInfo.getPath();
        if (path == null || path.length() == 0) {
            return;
        }

        boolean isPreview = apiInfo.isAllow(Flag.ALLOW_PREVIEW) && HttpRequestUtils.intranet(request);
        String group = request.headers().get("group");

        //使用了nacos存放ip和端口
        //nacos 的暂不支持权重和preview的概念
        //example: pro|http://${GisServer}$/api?id=123
        String serviceName = "";
        if (StringUtils.isEmpty(group)) {
            serviceName = GatewayNamingService.findServiceName(path);
        } else {
            serviceName = namingService.findServiceName(path, group, apiInfo.getId());
        }
        if (StringUtils.isNotEmpty(serviceName)) {
            setPath(apiInfo, context, path, serviceName, isPreview);
            return;
        }


        //允许内网使用preview
        if (isPreview) {
            String[] arrays = path.split(";");
            path = arrays.length > 1 ? arrays[1] : arrays[0];
        } else {
            //其他情况都取production即可(防止只去掉启用内外网,而配置没做修改的问题)
            path = path.split(";")[0];
        }

        String[] clients = path.split(",");
        String currentClient = "";
        //只有一个地址
        if (clients.length == 1) {
            currentClient = clients[0].split("\\|")[0];
        } else {
            //有多个
            List<Pair<String, Integer>> list = Arrays.stream(clients).map(it -> {
                String[] ss = it.split("\\|");
                String url = ss[0];
                int weight = 1;
                if (ss.length > 1) {
                    weight = Integer.valueOf(ss[1]);
                }
                Pair<String, Integer> pair = Pair.of(url, weight);
                return pair;
            }).collect(Collectors.toList());
            String v = Utils.random(list);
            currentClient = v;
        }
        context.getAttachments().put("Path", currentClient);
    }

    private void setPath(ApiInfo apiInfo, FilterContext context, String path, String serviceName, boolean isPreview) {
        if (isPreview) {
            serviceName = GatewayNamingService.getPreServiceName(serviceName);
        }
        String tag = context.getAttachment(Tag, "");
        String addr = namingService.getOneAddr(serviceName, tag);
        if (StringUtils.isEmpty(addr)) {
            //没有可用的ip:port列表
            throw new GatewayException("no http provider id:" + apiInfo.getId() + " url:" + apiInfo.getUrl() + " serviceName: " + serviceName);
        }
        if (path.contains("|")) {
            path = path.split("\\|")[1];
        }
        String currentClient = path.replaceFirst("\\$\\{.*\\}\\$", addr);
        context.getAttachments().put("Path", currentClient);
    }

    @Override
    public boolean rpcFilter() {
        return true;
    }
}
