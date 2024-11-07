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
import com.xiaomi.data.push.uds.processor.StreamCallback;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
            log.debug("server receive:id:{}:{}:{}:{}:{}", command.getId(), command.isRequest(), command.getApp(), command.getCmd(), command.getSerializeType());
            if (command.isRequest()) {
                command.setChannel(ctx.channel());
                Pair<UdsProcessor, ExecutorService> pair = this.m.get(command.getCmd());
                if (null != pair) {
                    UdsProcessor<UdsCommand, UdsCommand> processor = pair.getKey();
                    // 判断是否为流式处理
                    if (processor.isStreamProcessor()) {
                        handleStreamRequest(ctx, command, processor);
                    } else {
                        handleNormalRequest(pair.getValue(), ctx, command, processor);
                    }
                } else {
                    log.warn("processor is null cmd:{},id:{}", command.getCmd(), command.getId());
                }
            } else {
                Optional.ofNullable(UdsServer.reqMap.get(command.getId())).ifPresent(f -> f.complete(command));
            }
        } finally {
            ReferenceCountUtil.release(_msg);
        }
    }

    private void handleNormalRequest(ExecutorService pool, ChannelHandlerContext ctx, UdsCommand command, UdsProcessor<UdsCommand, UdsCommand> processor) {
        pool.submit(() -> {
            log.debug("server received:{}", command.getId());
            UdsCommand res = processor.processRequest(command);
            if (null != res) {
                Send.send(ctx.channel(), res);
            }
        });
    }


    private void handleStreamRequest(ChannelHandlerContext ctx, UdsCommand command,
                                     UdsProcessor<UdsCommand, UdsCommand> processor) {

        String streamId = command.getAttachments().getOrDefault(
                MessageTypes.STREAM_ID_KEY,
                UUID.randomUUID().toString()
        );

        StreamCallback callback = new StreamCallback() {
            @Override
            public void onContent(String content) {
                sendStreamContent(ctx, command, streamId, content);
            }

            @Override
            public void onComplete() {
                sendCompleteResponse(ctx, command, streamId);
            }

            @Override
            public void onError(Throwable error) {
                sendErrorResponse(ctx, command, error.getMessage());
            }
        };

        // 执行流式处理
        processor.processStream(command, callback);
    }


    private void sendErrorResponse(ChannelHandlerContext ctx, UdsCommand command, String error) {
        UdsCommand response = UdsCommand.createResponse(command);
        response.setCode(-1);
        response.setMessage(error);
        Send.send(ctx.channel(), response);
    }


    private void sendCompleteResponse(ChannelHandlerContext ctx, UdsCommand request, String streamId) {
        UdsCommand response = UdsCommand.createResponse(request);
        Map<String, String> attachments = response.getAttachments();
        attachments.put(MessageTypes.TYPE_KEY, MessageTypes.TYPE_OPENAI);
        attachments.put(MessageTypes.STREAM_ID_KEY, streamId);
        attachments.put(MessageTypes.STATUS_KEY, "complete");
        Send.send(ctx.channel(), response);
    }


    private void sendStreamContent(ChannelHandlerContext ctx, UdsCommand request, String streamId, String content) {
        UdsCommand response = UdsCommand.createResponse(request);
        Map<String, String> attachments = response.getAttachments();
        attachments.put(MessageTypes.TYPE_KEY, MessageTypes.TYPE_OPENAI);
        attachments.put(MessageTypes.STREAM_ID_KEY, streamId);
        attachments.put(MessageTypes.CONTENT_KEY, content);
        Send.send(ctx.channel(), response);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UdsServerContext.ins().active(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Attribute<String> attr = ctx.channel().attr(app);
        String v = attr.get();
        log.error("server channelInactive:{},{},{}", app, v, ctx.channel().id());
        if (null != v) {
            UdsServerContext.ins().remove(v);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught,{}:{}", ctx.channel().id(), cause);
        Attribute<String> attr = ctx.channel().attr(app);
        String v = attr.get();
        if (null != v) {
            UdsServerContext.ins().remove(v);
        }
        ctx.close();
    }


}
