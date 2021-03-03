
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
package com.xiaomi.data.push.uds.context;

import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.po.UdsCommand;
import io.netty.channel.Channel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class UdsServerContext {

    private ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    @Setter
    private NetListener listener = new NetListener() {
        @Override
        public void handle(NetEvent event) {

        }
    };

    private UdsServerContext() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//            health();
        }, 0, 5, TimeUnit.SECONDS);

    }

    public void put(String id, Channel channel) {
        this.channelMap.put(id, channel);
    }

    public void remove(String id) {
        log.info("remove id:{}", id);
        this.listener.handle(new NetEvent(NetType.inactive, id));
        this.channelMap.remove(id);
    }

    public Channel channel(String id) {
        return this.channelMap.get(id);
    }

    /**
     * 网络活跃了,也就是链接上了
     *
     * @param channel
     */
    public void active(Channel channel) {
        log.info("active:{}", channel);
        this.listener.handle(new NetEvent(NetType.active, channel));
    }

    private static final class LazyHolder {
        private static final UdsServerContext ins = new UdsServerContext();
    }

    public static final UdsServerContext ins() {
        return LazyHolder.ins;
    }


    public void health() {
        channelMap.values().forEach(it -> {
            UdsCommand request = UdsCommand.createRequest();
            request.setCmd("health");
            request.setMethodName("health");
            Send.send(it, request);
        });
    }
}
