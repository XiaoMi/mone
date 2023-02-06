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

import com.xiaomi.youpin.gateway.TeslaConstants;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 * <p>
 * 验证前端传过来的token
 */
@Component
@FilterOrder(Integer.MIN_VALUE + 13)
public class TokenFilter extends RequestFilter {

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        //是否要校验token
        if (apiInfo.isAllow(Flag.ALLOW_TOKEN)) {
            String token = apiInfo.getToken();

            String frontToken = request.headers().get(TeslaConstants.FrontHeaderToken);
            if (StringUtils.isEmpty(frontToken)) {
                return HttpResponseUtils.create(Result.fail(GeneralCodes.Forbidden, HttpResponseStatus.FORBIDDEN.reasonPhrase(), "frontToken is empty"));
            }

            if (!token.equals(frontToken)) {
                return HttpResponseUtils.create(Result.fail(GeneralCodes.Forbidden, HttpResponseStatus.FORBIDDEN.reasonPhrase(), "frontToken is wrong"));
            }

        }
        return invoker.doInvoker(context, apiInfo, request);
    }
}
