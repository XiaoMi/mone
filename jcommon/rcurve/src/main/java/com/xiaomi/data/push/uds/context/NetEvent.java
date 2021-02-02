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
import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class NetEvent {

    private NetType type;

    private String app;

    private Channel channel;

    public NetEvent(NetType type, String app) {
        this.type = type;
        this.app = app;
    }

    public NetEvent() {
    }

    public NetEvent(NetType type) {
        this.type = type;
    }

    public NetEvent(NetType type, Channel channel) {
        this.type = type;
        this.channel = channel;
    }
}
