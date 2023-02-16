package com.xiaomi.mone.log.agent.rpc.processor;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.log.agent.channel.ChannelDefine;
import com.xiaomi.mone.log.agent.channel.ChannelEngine;
import com.xiaomi.mone.log.agent.channel.locator.ChannelDefineRpcLocator;
import com.xiaomi.mone.log.api.model.meta.LogCollectMeta;
import com.xiaomi.mone.log.api.model.vo.LogCmd;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Component
public class LogProcessor implements NettyRequestProcessor {

    private Gson gson = new Gson();

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws Exception {
        LogCollectMeta req = remotingCommand.getReq(LogCollectMeta.class);

        log.info("logCollect config req:{}", gson.toJson(req));

        RemotingCommand response = RemotingCommand.createResponseCommand(LogCmd.logRes);
        response.setBody("ok".getBytes());
        log.info("【config change】receive data：{}", gson.toJson(req));
        metaConfigEffect(req);
        log.info("config change success");
        return response;
    }

    private synchronized void metaConfigEffect(LogCollectMeta req) {
        ChannelEngine channelEngine = Ioc.ins().getBean(ChannelEngine.class);
        CompletableFuture<Void> reFreshFuture = CompletableFuture.runAsync(() -> {
        });
        CompletableFuture<Void> stopChannelFuture = CompletableFuture.runAsync(() -> {
        });
        // 是否初始化完成，没完成等待30s后执行
        int count = 0;
        while (true) {
            if (!channelEngine.isInitComplete()) {
                try {
                    TimeUnit.SECONDS.sleep(5L);
                    ++count;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (channelEngine.isInitComplete() || count >= 20) {
                break;
            }
        }
        if (CollectionUtils.isNotEmpty(req.getAppLogMetaList())) {
            reFreshFuture = CompletableFuture.runAsync(() -> {
                List<ChannelDefine> channelDefines = ChannelDefineRpcLocator.agentTail2ChannelDefine(ChannelDefineRpcLocator.logCollectMeta2ChannelDefines(req));
                channelEngine.refresh(channelDefines);
            });
        }
        if (CollectionUtils.isNotEmpty(req.getPodNames())) {
            stopChannelFuture = CompletableFuture.runAsync(() -> {
                channelEngine.stopChannelFile(req.getPodNames());
            });
        }
        CompletableFuture.allOf(reFreshFuture, stopChannelFuture).join();
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }


    @Override
    public int cmdId() {
        return LogCmd.logReq;
    }
}
