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
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 */
@Slf4j
public class UdsClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ConcurrentHashMap<String, Pair<UdsProcessor<UdsCommand, UdsCommand>,ExecutorService>> processorMap;


    public UdsClientHandler(ConcurrentHashMap<String, Pair<UdsProcessor<UdsCommand, UdsCommand>,ExecutorService>> processorMap) {
        this.processorMap = processorMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        UdsCommand command = new UdsCommand();
        command.decode(msg);
        log.debug("client received:{}", command);
        if (command.isRequest()) {
            command.setChannel(ctx.channel());
            Pair<UdsProcessor<UdsCommand, UdsCommand>, ExecutorService> pair = this.processorMap.get(command.getCmd());
            if (null != pair) {
                UdsProcessor processor = pair.getKey();
                pair.getValue().submit(()->{
                    SafeRun.run(() -> {
                        Object res = processor.processRequest(command);
                        if (null != res) {
                            Send.send(ctx.channel(), (UdsCommand) res);
                        }
                    });
                });
            } else {
                log.warn("processor is null cmd:{}", command.getCmd());
            }
        } else {
            Optional.ofNullable(UdsClient.reqMap.get(command.getId())).ifPresent(f -> f.complete(command));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        UdsClientContext.ins().channelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught:{}", cause);
        UdsClientContext.ins().exceptionCaught(cause);
        ctx.close();
    }
}
