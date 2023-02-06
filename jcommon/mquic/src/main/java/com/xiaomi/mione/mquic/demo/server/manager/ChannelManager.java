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

package com.xiaomi.mione.mquic.demo.server.manager;

import com.xiaomi.mione.mquic.demo.common.Safe;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
 */
public class ChannelManager {

    private ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    public void addChannel(Channel channel) {
        channelMap.put(channel.id().asLongText(), channel);
    }


    public void removeChannel(Channel channel) {
        channelMap.remove(channel.id().asLongText());
    }

    public void sendMessage(final String message) {
        channelMap.values().forEach(c -> Safe.run(() -> c.writeAndFlush(Unpooled.wrappedBuffer(message.getBytes()))));
    }


}
