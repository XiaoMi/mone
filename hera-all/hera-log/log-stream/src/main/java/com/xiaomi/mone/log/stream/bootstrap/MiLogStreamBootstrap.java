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

package com.xiaomi.mone.log.stream.bootstrap;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/22 13:58
 */

import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_STREAM_SERVER_NAME;
import static com.xiaomi.mone.log.stream.common.util.StreamUtils.*;

@Slf4j
public class MiLogStreamBootstrap {

    public static void main(String[] args) throws IOException {
        getConfigFromNacos();
        Ioc.ins().putBean(getOkHttpClient()).init("com.xiaomi.mone.log.stream", "com.xiaomi.youpin.docean");
        long initDelay = 0;
        long intervalTime = 2;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> log.info("I am health,time:{}", LocalDateTime.now()), initDelay, intervalTime, TimeUnit.MINUTES);
        serviceRegister(Config.ins().get("app_name", DEFAULT_STREAM_SERVER_NAME));
        System.in.read();
    }

    private static void serviceRegister(String serviceName) {
        String server_addr = Config.ins().get("nacos_config_server_addr", "");
        try {
            getNacosNaming(server_addr).registerInstance(serviceName, buildInstance(serviceName));
        } catch (Exception e) {
            log.error("register stream service error,nacos address:{}", server_addr, e);
        }
    }

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(5 * 60, TimeUnit.SECONDS).writeTimeout(5 * 60, TimeUnit.SECONDS).connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES)).build();
    }

}
