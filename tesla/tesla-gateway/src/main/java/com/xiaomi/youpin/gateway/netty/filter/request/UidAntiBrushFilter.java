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

import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.common.Keys;
import com.xiaomi.youpin.gateway.common.Utils;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FilterOrder(1000 + 2)
public class UidAntiBrushFilter extends RequestFilter {

    @Autowired
    private Redis redis;

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {

        if (apiInfo.isAllow(Flag.UID_ANTI_BRUSH)) {

            if (StringUtils.isEmpty(context.getUid())
                    || context.getUid().equals("0")) {
                return HttpResponseUtils.create(Result.fail(GeneralCodes.NotAuthorized, HttpResponseStatus.UNAUTHORIZED.reasonPhrase(), "uid is 0"));
            }

            String key = Keys.uidAntiBrushKey(Utils.md5(apiInfo.getUrl()), context.getUid());
            try {
                long value = redis.incr(key);
                if (value == 1L) {
                    redis.expire(key, 60);
                }
                if (value > apiInfo.getUidAntiBrushLimit()) {
                    //429
                    return HttpResponseUtils.create(Result.fail(GeneralCodes.TOO_MANY_REQUESTS, "您的操作太频繁啦，请稍后重试哦~", context.getUid()));
                }
            } catch (Throwable ex) {
                log.warn("redis incr error:{}", ex.getMessage());
            }
        }

        return invoker.doInvoker(context, apiInfo, request);
    }

}
