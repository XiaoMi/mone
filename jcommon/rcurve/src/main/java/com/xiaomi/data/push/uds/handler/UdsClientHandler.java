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
import java.util.concurrent.CompletableFuture;
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
        log.debug("client received:{},{}", command.getId(), command.isRequest());
        if (command.isRequest()) {
            command.setChannel(ctx.channel());
            Pair<UdsProcessor<UdsCommand, UdsCommand>, ExecutorService> pair = this.processorMap.get(command.getCmd());
            if (null != pair) {
                UdsProcessor processor = pair.getKey();
                pair.getValue().submit(()->{
                    SafeRun.run(() -> {
                        log.debug("client received:{}", command.getId());
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
            Optional.ofNullable(UdsClient.reqMap.get(command.getId())).ifPresent(f -> {
                if (Boolean.TRUE.toString().equals(String.valueOf(f.get("async")))) {
                    Object res = null;
                    try {
                        res = processResult(command, (Class<?>) f.get("returnType"));
                        if (command.getCode() == 0) {
                            ((CompletableFuture)f.get("future")).complete(res);
                        } else {
                            ((CompletableFuture)f.get("future")).completeExceptionally(new RuntimeException(res.toString()));
                        }
                    } catch (Exception e) {
                        log.error("async response error,", e);
                        ((CompletableFuture)f.get("future")).completeExceptionally(e);
                    }
                    UdsClient.reqMap.remove(command.getId());
                } else {
                    ((CompletableFuture)f.get("future")).complete(command);
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("client channelInactive:{}",ctx.channel().id());
        UdsClientContext.ins().channelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught:{},{}",ctx.channel().id(), cause);
        UdsClientContext.ins().exceptionCaught(cause);
        ctx.close();
    }

    private Object processResult(UdsCommand res, Class<?> returnType) {
        if (returnType.equals(void.class)) {
            return null;
        }
        //返回结果就是空
        if (res.getAtt("res_is_null", "false").equals("true")) {
            return null;
        }
        Object result = res.getData(returnType);
        log.debug("call sidecar:{} receive:{}", "", result);
        return result;
    }
}
