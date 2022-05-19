package com.xiaomi.data.push.uds.handler;

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
import java.util.concurrent.Executors;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 */
@Slf4j
public class UdsClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ConcurrentHashMap<String, UdsProcessor> processorMap;

    private ExecutorService pool = Executors.newFixedThreadPool(200);

    public UdsClientHandler(ConcurrentHashMap<String, UdsProcessor> processorMap) {
        this.processorMap = processorMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        UdsCommand command = new UdsCommand();
        command.decode(msg);
        log.debug("client received:{}", command);
        if (command.isRequest()) {
            command.setChannel(ctx.channel());
            UdsProcessor processor = this.processorMap.get(command.getCmd());
            if (null != processor) {
                pool.submit(() -> processor.processRequest(command));
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
        log.error("exceptionCaught:{}", cause.getMessage());
        UdsClientContext.ins().exceptionCaught(cause);
        ctx.close();
    }
}
