package com.xiaomi.data.push.uds.handler;

import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 */
@Slf4j
public class UdsServerHandler extends ChannelInboundHandlerAdapter {

    public static AttributeKey<String> app = AttributeKey.valueOf("app");


    private ExecutorService pool = Executors.newFixedThreadPool(200);


    private Map<String, UdsProcessor> m;


    public UdsServerHandler(ConcurrentHashMap<String, UdsProcessor> processorMap) {
        this.m = processorMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object _msg) {
        ByteBuf msg = (ByteBuf) _msg;
        UdsCommand command = new UdsCommand();
        command.decode(msg);
        log.debug("server receive:{}:{}:{}", command.getApp(), command.getCmd(), command.getSerializeType());
        if (command.isRequest()) {
            command.setChannel(ctx.channel());
            UdsProcessor processor = this.m.get(command.getCmd());
            if (null != processor) {
                pool.submit(() -> processor.processRequest(command));
            } else {
                log.warn("processor is null cmd:{}", command.getCmd());
            }
        } else {
            Optional.ofNullable(UdsServer.reqMap.get(command.getId())).ifPresent(f -> f.complete(command));
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
