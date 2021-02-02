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

import com.xiaomi.youpin.gateway.cache.ApiRouteCache;
import com.xiaomi.youpin.gateway.dispatch.Dispatcher;
import com.xiaomi.youpin.gateway.netty.filter.RequestFilterChain;

/**
 * @author goodjava@qq.com
 */
public class HttpProxyServerBootstrap {

    private int port = 8080;
    private final Dispatcher dispatcher;
    private final RequestFilterChain filterChain;
    private final ApiRouteCache apiRouteCache;

    public HttpProxyServerBootstrap(Dispatcher dispatcher, RequestFilterChain filterChain, ApiRouteCache apiRouteCache) {
        this.dispatcher = dispatcher;
        this.filterChain = filterChain;
        this.apiRouteCache = apiRouteCache;
    }

    public HttpProxyServerBootstrap port(int port) {
        this.port = port;
        return this;
    }


    public HttpProxyServer start() {
        return build().start();
    }


    private HttpProxyServer build() {
        return new HttpProxyServer(this.port, dispatcher, filterChain, apiRouteCache);
    }

}
