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

package com.xiaomi.youpin.gateway.netty;

import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author goodjava@qq.com
 */
public class GatewayInvoker implements Invoker {

    @Override
    public FullHttpResponse doInvoker(FilterContext ctx, ApiInfo apiInfo, FullHttpRequest request) {
        return HttpResponseUtils.create("gateway invoker");
    }
}
