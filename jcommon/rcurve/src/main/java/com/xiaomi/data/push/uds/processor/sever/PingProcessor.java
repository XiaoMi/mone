package com.xiaomi.data.push.uds.processor.sever;

import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.handler.UdsServerHandler;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class PingProcessor implements UdsProcessor {

    @Override
    public void processRequest(UdsCommand request) {
        Attribute<String> attr = request.getChannel().attr(UdsServerHandler.app);
        attr.setIfAbsent(request.getApp());
        log.info("ping:{}", request.getApp());
        UdsServerContext.ins().put(request.getApp(), request.getChannel());
        UdsCommand res = UdsCommand.createResponse(request);
        res.setData("pong");
        Send.sendResponse(request.getChannel(), res);
    }

}
