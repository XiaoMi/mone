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

package com.xiaomi.youpin.gateway.filter;

import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.infra.rpc.Result;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * 最小权限,你的filter失败了并不会影响下游filter执行
 * 对结果的修改自己要保障安全
 */
@Slf4j
public abstract class CustomRequestFilter extends RequestFilter {

    @Override
    public final FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (this.allow(apiInfo)) {
            try {
                log.debug("start do filter: {}", this.getDef().getName());
                context.setNext(false);
                return execute(context, invoker, apiInfo, request);
            } catch (Throwable ex) {
                log.error("invoke custom filter:{} error:{}", this.getDef().getName(), ex.getMessage());
                log.error("invoke custom, trace: {}", ex.getStackTrace());
                //filter chain 已经执行过了,不再第二次执行了
                if (!context.isNext()) {
                    return invoker.doInvoker(context, apiInfo, request);
                }
                return HttpResponseUtils.create(Result.fromException(ex));
            }
        } else {
            return invoker.doInvoker(context, apiInfo, request);
        }
    }

    public FullHttpResponse next(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        context.setNext(true);
        return invoker.doInvoker(context, apiInfo, request);
    }


    public abstract FullHttpResponse execute(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request);
}
