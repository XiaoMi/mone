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

package com.xiaomi.youpin.gateway.protocol;

import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public abstract class IRpcClient {

    public abstract FullHttpResponse call(final FilterContext ctx, final ApiInfo rpcDo, final FullHttpRequest servletRequest);

    protected static void setFilterContextHeaders(FilterContext ctx, Map<String, String> headers) {
        ctx.getHeaders().forEach((k, v) -> headers.put(k, v));
    }

}
