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

import io.netty.channel.Channel;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author goodjava@qq.com
 */
public class UdsClientContext {

    @Setter
    private NetListener listener = new NetListener() {
        @Override
        public void handle(NetEvent event) {

        }
    };

    public AtomicReference<Channel> channel = new AtomicReference<>();

    private UdsClientContext() {
    }

    private static final class LazyHolder {
        private static final UdsClientContext ins = new UdsClientContext();
    }

    public static final UdsClientContext ins() {
        return LazyHolder.ins;
    }

    public void exceptionCaught(Throwable cause) {
        listener.handle(new NetEvent(NetType.exception));
    }

    public void channelInactive() {
        listener.handle(new NetEvent(NetType.inactive));
    }


}
