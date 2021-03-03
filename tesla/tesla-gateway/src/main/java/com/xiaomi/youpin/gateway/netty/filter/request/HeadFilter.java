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
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.xiaomi.youpin.qps.QpsAop;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Component
@FilterOrder(Integer.MIN_VALUE + 10)
public class HeadFilter extends RequestFilter {


    @Autowired
    private QpsAop qpsAop;


    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        qpsAop.incr(String.valueOf(apiInfo.getId()));
        if (null == apiInfo) {
            return HttpResponseUtils.create(Result.fail(GeneralCodes.NotFound, HttpResponseStatus.NOT_FOUND.reasonPhrase(), "apiInfo is null"));
        } else {
            //服务下线了
            if (apiInfo.isAllow(Flag.OFF_LINE)) {
                return HttpResponseUtils.create(Result.fail(GeneralCodes.METHOD_NOT_ALLOWED, HttpResponseStatus.METHOD_NOT_ALLOWED.reasonPhrase(), "api is offline"));
            }
        }
        log.debug("head filter id:{} url:{}", apiInfo.getId(), apiInfo.getUrl());
        return invoker.doInvoker(context, apiInfo, request);
    }

}
