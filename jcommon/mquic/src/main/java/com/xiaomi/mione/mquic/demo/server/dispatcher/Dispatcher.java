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

package com.xiaomi.mione.mquic.demo.server.dispatcher;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
 */
@Slf4j
public class Dispatcher {

    private ExecutorService pool = Executors.newFixedThreadPool(200);

    public void execute(String message, Channel channel) {
        log.info("message:{}", message);
        pool.submit(() -> {
            byte[] res = ("ok:" + message).getBytes();
            channel.writeAndFlush(Unpooled.wrappedBuffer(res));
        });
    }

}
