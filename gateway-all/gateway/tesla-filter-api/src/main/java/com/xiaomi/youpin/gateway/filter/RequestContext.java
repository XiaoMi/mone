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

package com.xiaomi.youpin.gateway.filter;

import com.youpin.xiaomi.tesla.plugin.bo.Message;
import com.youpin.xiaomi.tesla.plugin.bo.User;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 */
@Data
public class RequestContext {

    private CountDownLatch latch;

    private String ip;

    private User user;

    private String uri;

    private Consumer<User> regConsumer;

    private Consumer<Message> sendConsumer;

    private Function<String, Map<String, User>> groupFunction;

    private Consumer<User> pingConsumer;

    private Channel channel;

    private long begin;

    private String callId;

    public ByteBuf byteBuf(byte[] data) {
        return Unpooled.wrappedBuffer(data);
    }

    public ByteBuf byteBuf(byte[] data, boolean direct) {
        if (direct) {
            return this.channel.alloc().directBuffer(data.length).writeBytes(data);
        }
        return byteBuf(data);
    }
}
