package com.xiaomi.youpin.docean.plugin.dmesh.processor.client;

import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 1/19/21
 *
 * mesh服务会发送一些mesh信息给client,这个processor就是处理这些消息的
 *
 */
@Slf4j
public class MessageProcessor implements UdsProcessor {

    @Override
    public void processRequest(UdsCommand request) {
        String data = request.getData(String.class);
        log.info("server:{}", data);
    }

    @Override
    public String cmd() {
        return "message";
    }
}
