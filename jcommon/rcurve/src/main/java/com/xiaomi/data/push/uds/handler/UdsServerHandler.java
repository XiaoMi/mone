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

package com.xiaomi.data.push.uds.handler;

import com.xiaomi.data.push.common.Pair;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.Cons;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 */
@Slf4j
public class UdsServerHandler extends ChannelInboundHandlerAdapter {

    public static AttributeKey<String> app = AttributeKey.valueOf("app");

    private Map<String, Pair<UdsProcessor, ExecutorService>> m;


    public UdsServerHandler(ConcurrentHashMap<String, Pair<UdsProcessor, ExecutorService>> processorMap) {
        this.m = processorMap;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object _msg) {
        try {
            ByteBuf msg = (ByteBuf) _msg;
            UdsCommand command = new UdsCommand();
            command.decode(msg);
            command.putAtt(Cons.SIDE_TYPE_SERVER, Boolean.TRUE.toString());
            command.putAtt(Cons.SIDE_TYPE_CLIENT, Boolean.FALSE.toString());
            log.debug("server receive:{}:{}:{}", command.getApp(), command.getCmd(), command.getSerializeType());
            if (command.isRequest()) {
                command.setChannel(ctx.channel());
                Pair<UdsProcessor, ExecutorService> pair = this.m.get(command.getCmd());
                if (null != pair) {
                    UdsProcessor<UdsCommand, UdsCommand> processor = pair.getKey();
                    pair.getValue().submit(() -> {
                        UdsCommand res = processor.processRequest(command);
                        if (null != res) {
                            Send.send(ctx.channel(), res);
                        }
                    });
                } else {
                    log.warn("processor is null cmd:{}", command.getCmd());
                }
            } else {
                Optional.ofNullable(UdsServer.reqMap.get(command.getId())).ifPresent(f -> f.complete(command));
            }
        } finally {
            ReferenceCountUtil.release(_msg);
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UdsServerContext.ins().active(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Attribute<String> attr = ctx.channel().attr(app);
        String v = attr.get();
        if (null != v) {
            UdsServerContext.ins().remove(v);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        Attribute<String> attr = ctx.channel().attr(app);
        String v = attr.get();
        if (null != v) {
            UdsServerContext.ins().remove(v);
        }
        ctx.close();
    }


}
