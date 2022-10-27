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
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import com.youpin.xiaomi.tesla.bo.ResponseConfig;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 * 进行mock处理
 */
@Component
@FilterOrder(1000)
public class MockFilter extends RequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(MockFilter.class);

    @Autowired
    private Redis redis;

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (apiInfo.isAllow(Flag.ALLOW_MOCK)) {
            logger.debug("mock filter invoker id:{}", apiInfo.getId());
            ResponseConfig rc = new ResponseConfig();
            rc.setContentType(HttpResponseUtils.ContentTypeJson);
            context.setResponseConfig(rc);
            String mockDataId = String.valueOf(apiInfo.getId());
            String data = redis.get(Keys.mockKey(mockDataId));
            return HttpResponseUtils.create(data, context.getResponseConfig().getContentType());
        }
        return invoker.doInvoker(context, apiInfo, request);

    }
}
