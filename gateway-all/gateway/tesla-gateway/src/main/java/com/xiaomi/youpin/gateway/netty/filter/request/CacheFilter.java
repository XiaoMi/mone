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

import com.google.common.base.Joiner;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gateway.common.*;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Component
@FilterOrder(2000)
public class CacheFilter extends RequestFilter {

    @Autowired
    private Redis redis;

    /**
     * 默认的缓存时间
     */
    private static long Default_Cache_Time = TimeUnit.SECONDS.toMillis(60);

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        //直接返回mock数据
        if (apiInfo.isAllow(Flag.ALLOW_CACHE)) {
            log.debug("CacheFilter.doFilter, apiInfo id : {}", apiInfo.getId());
            String key = getKey(context, apiInfo, request);
            String value = redis.get(key);
            if (null != value) {
                return HttpResponseUtils.create(value);
            } else {
                FullHttpResponse res = invoker.doInvoker(context, apiInfo, request);
                try {
                    redis.set(key, HttpResponseUtils.getContent(res), (int) getCacheTime(apiInfo));
                } catch (Throwable ex) {
                    log.warn("redis set error:{}", ex.getMessage());
                }
                return res;
            }
        }
        return invoker.doInvoker(context, apiInfo, request);
    }



    private long getCacheTime(ApiInfo info) {
        long cacheTime = info.getCacheTime();
        if (0 == cacheTime) {
            return Default_Cache_Time;
        }
        return cacheTime;
    }

    private String getKey(FilterContext context, ApiInfo apiInfo, FullHttpRequest request) {
        String uid = "";
        if (StringUtils.isNotEmpty(context.getUid())) {
            uid = context.getUid();
        }
        String key;
        String httpMethod = request.method().toString().toUpperCase();
        if (httpMethod.equals("GET")) {
            String queryString = HttpRequestUtils.getQueryString(request);
            key = Keys.cacheKey(apiInfo.getId(), Utils.md5(queryString));
        } else if (httpMethod.equals("POST")) {
            byte[] body = HttpRequestUtils.getRequestBody(request);
            key = Keys.cacheKey(apiInfo.getId(), Utils.md5(new String(body)));
        } else {
            key = Keys.cacheKey(apiInfo.getId(), "");
        }
        return Joiner.on("_").join(key,uid);
    }

}
