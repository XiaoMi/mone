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

import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.Flag;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.service.UserService;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 * <p>
 * 验证权限filter,会用cookie换取uid
 */
@Slf4j
@Component
@FilterOrder(1000 + 1)
public class AuthFilter extends RequestFilter {


    @Autowired
    private UserService userService;

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (apiInfo.isAllow(Flag.ALLOW_AUTH)) {
            String uid = getUid(request);
            context.setUid(uid);
            context.setUserAgent(request.headers().get("user-agent"));
        }
        return invoker.doInvoker(context, apiInfo, request);
    }

    /**
     * 获取uid
     *
     * @param request
     * @return
     */
    private String getUid(FullHttpRequest request) {
        String cookie = request.headers().get("cookie");
        if (StringUtils.isNotEmpty(cookie)) {
            //call remote service
            try {
                long uid = userService.getUidFromCookie(cookie);
                return String.valueOf(uid);
            } catch (Throwable e) {
                log.warn("getUid cookie:{} error:{}", cookie, e.getMessage());
                return "0";
            }
        }
        return "0";
    }
}
