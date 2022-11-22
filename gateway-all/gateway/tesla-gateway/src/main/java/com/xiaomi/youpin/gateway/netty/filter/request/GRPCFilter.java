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

import com.xiaomi.youpin.gateway.RouteType;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.sidecar.SideCarFilterService;
import com.xiaomi.youpin.gateway.sidecar.bo.SideCarEnum;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xiaomi.youpin.gateway.filter.FilterContext.New_Route_Type;


@Component
@FilterOrder(5000)
@Slf4j
public class GRPCFilter extends RequestFilter {

    @Autowired
    private SideCarFilterService sideCarFilterService;

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        int routeType = apiInfo.getRouteType();
        if (StringUtils.isNotEmpty(context.getAttachments().get(New_Route_Type))) {
            routeType = Integer.valueOf(context.getAttachments().get(New_Route_Type));
        }
        if (routeType == RouteType.GRPC.type()) {
            //启用sidecar模式
            if (sideCarFilterService.supportSideCar(SideCarEnum.grpc)) {
                return sideCarFilterService.callGRPCFilterSideCar(context, apiInfo, request);
            }
            return HttpResponseUtils.create(Result.fail(GeneralCodes.NotFound, "opening soon", "not yet"));
        }
        return invoker.doInvoker(context, apiInfo, request);
    }

    @Override
    public boolean rpcFilter() {
        return true;
    }
}
