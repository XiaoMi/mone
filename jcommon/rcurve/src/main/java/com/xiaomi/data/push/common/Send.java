package com.xiaomi.data.push.common;

import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.codes.ICodes;
import com.xiaomi.data.push.uds.po.UdsCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public abstract class Send {

    public static void send(Channel channel, UdsCommand command) {
        if (null == channel || !channel.isOpen()) {
            log.warn("channel is close");
            return;
        }
        command.setSerializeType(RcurveConfig.ins().getCodeType());
        ByteBuf buf = command.encode();
        channel.writeAndFlush(buf);
    }

    public static void sendResponse(Channel channel, UdsCommand response) {
        if (null == channel || !channel.isOpen()) {
            log.warn("channel is close");
            return;
        }
        ByteBuf buf = response.encode();
        channel.writeAndFlush(buf);
    }


    public static void sendMessage(Channel channel, String message) {
        UdsCommand msg = UdsCommand.createRequest();
        msg.setCmd("message");
        msg.setData(message);
        send(channel, msg);
    }

}
