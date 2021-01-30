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

package com.xiaomi.youpin.tesla.rcurve.proxy.bootstrap;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.tesla.rcurve.proxy.ProxyServer;
import com.xiaomi.youpin.tesla.rcurve.proxy.common.RcurveShutdownHook;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @author dingpei
 */
@Slf4j
public class RcurveBootstrap {

    public static void main(String... args) {
        HttpServerConfig.HTTP_POOL_SIZE = 1;
        HttpServerConfig.HTTP_POOL_QUEUE_SIZE = 20000;
        ProxyServer proxyServer = new ProxyServer();
        Ioc.ins().putBean(proxyServer).init("com.xiaomi" );
        Runtime.getRuntime().addShutdownHook(new Thread(new RcurveShutdownHook()));
        proxyServer.openProxy();
        log.info("tesla rcurve start finish");
    }
}
