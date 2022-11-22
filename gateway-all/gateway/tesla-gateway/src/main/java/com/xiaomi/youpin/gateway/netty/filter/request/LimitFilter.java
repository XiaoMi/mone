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
import com.xiaomi.youpin.gateway.context.GatewayServerContext;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
//@Component
@FilterOrder(Integer.MIN_VALUE + 14)
@Slf4j
public class LimitFilter extends RequestFilter {

    private static final int LimitNum = 100000;

    @Autowired
    private GatewayServerContext gatewayServerContext;

    @Autowired
    private ConfigService configService;


    private ConcurrentHashMap<Long, Semaphore> map = new ConcurrentHashMap<>();

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {

        if (configService.isCloseLimitFilter()) {
            return invoker.doInvoker(context, apiInfo, request);
        }

        long id = apiInfo.getId();

        final MutableInt limit = new MutableInt(apiInfo.getInvokeLimit());

        int agentNum = gatewayServerContext.getAgentNum().get();

        if (agentNum > 0) {
            limit.setValue(limit.getValue() / agentNum);
        }

        if (limit.getValue() <= 0) {
            limit.setValue(1);
        }

        Semaphore semaphore = map.compute(id, (aLong, sem) -> {
            if (null == sem) {
                return new Semaphore(LimitNum);
            }
            return sem;
        });

        int num = LimitNum / limit.getValue();

        try {
            boolean acquired = semaphore.tryAcquire(num, 1, TimeUnit.MILLISECONDS);
            if (acquired) {
                try {
                    return invoker.doInvoker(context, apiInfo, request);
                } finally {
                    semaphore.release(num);
                }
            } else {
                String info =
                        String.format("filter tryAcquire semaphore timeout, %dms, waiting thread nums: %d semaphoreAsyncValue: %d",
                                10,
                                semaphore.getQueueLength(),
                                semaphore.availablePermits()
                        );
                log.warn(info);
                //503
                return HttpResponseUtils.create(Result.fail(GeneralCodes.SERVICE_UNAVAILABLE, HttpResponseStatus.SERVICE_UNAVAILABLE.reasonPhrase(), info));
            }
        } catch (InterruptedException e) {
            //503
            log.warn("LimitFilter InterruptedException 503");
            return HttpResponseUtils.create(Result.fail(GeneralCodes.SERVICE_UNAVAILABLE, HttpResponseStatus.SERVICE_UNAVAILABLE.reasonPhrase(), "LimitFilter InterruptedException 503"));
        }

    }
}
