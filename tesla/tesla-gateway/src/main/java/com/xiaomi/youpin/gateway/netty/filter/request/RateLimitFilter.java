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

import com.google.common.util.concurrent.RateLimiter;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.context.GatewayServerContext;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * 限制:每秒不能超过的任务数
 */
@Component
@FilterOrder(Integer.MIN_VALUE + 15)
@Slf4j
public class RateLimitFilter extends RequestFilter {

    private ConcurrentHashMap<Long, RateLimiter> map = new ConcurrentHashMap<>();

    private static final int MAX_NUM = 1000000;

    @Autowired
    private GatewayServerContext gatewayServerContext;

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {

        if (!apiInfo.isAllow(Flag.USE_QPS_LIMIT)) {
            return invoker.doInvoker(context, apiInfo, request);
        }

        long id = apiInfo.getId();

        int permits = 1;

        final int limit = apiInfo.getRateInvokeLimit();

        permits *= MAX_NUM / limit;


        int agentNum = gatewayServerContext.getAgentNum().get();
        if (agentNum > 0) {
            permits *= agentNum;
        }

        RateLimiter limiter = map.compute(id, (aLong, l) -> {
            if (null == l) {
                return RateLimiter.create(MAX_NUM);
            }
            return l;
        });

        boolean acquired = limiter.tryAcquire(permits, 200, TimeUnit.MILLISECONDS);
        if (acquired) {
            return invoker.doInvoker(context, apiInfo, request);
        } else {
            log.warn("RateLimit acquire failure id:{}", apiInfo.getId());
            //503
            return HttpResponseUtils.create(Result.fail(GeneralCodes.SERVICE_UNAVAILABLE, HttpResponseStatus.SERVICE_UNAVAILABLE.reasonPhrase(), "RateLimit acquire failure id" + apiInfo.getId()));
        }

    }
}
