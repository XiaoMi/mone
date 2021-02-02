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

package com.xiaomi.youpin.tesla.bug.bootstrap;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2020/9/5
 */
@Slf4j
public class BugBootstrap {

    public static void main(String[] args) throws InterruptedException {
        Aop.ins().init(Maps.newLinkedHashMap());
        Ioc.ins().init("com.xiaomi.youpin");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("shutdown hook");
            Ioc.ins().destory();
        }));
        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder().port(8799).websocket(true).build());
        server.start();
    }


}
