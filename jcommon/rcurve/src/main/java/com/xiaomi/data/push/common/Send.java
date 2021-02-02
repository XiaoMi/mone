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

package com.xiaomi.data.push.common;

import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.codes.ICodes;
import com.xiaomi.data.push.uds.po.UdsCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public abstract class Send {

    public static void send(Channel channel, Object obj) {
        if (null == channel || !channel.isOpen()) {
            log.warn("channel is close");
            return;
        }
        ICodes codes = CodesFactory.getCodes(RcurveConfig.ins().getCodeType());
        byte[] data = codes.encode(obj);
        ByteBuf buf = Unpooled.buffer(1 + data.length);
        buf.writeByte(RcurveConfig.ins().getCodeType());
        buf.writeBytes(data);
        channel.writeAndFlush(buf);
    }

    public static void sendResponse(Channel channel, UdsCommand response) {
        if (null == channel || !channel.isOpen()) {
            log.warn("channel is close");
            return;
        }
        ICodes codes = CodesFactory.getCodes(response.getSerializeType());
        byte[] data = codes.encode(response);
        ByteBuf buf = Unpooled.buffer(1 + data.length);
        buf.writeByte(response.getSerializeType());
        buf.writeBytes(data);
        channel.writeAndFlush(buf);
    }


    public static void sendMessage(Channel channel, String message) {
        UdsCommand msg = UdsCommand.createRequest();
        msg.setCmd("message");
        msg.setData(message);
        Send.send(channel, msg);
    }

}
