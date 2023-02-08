package com.xiaomi.youpin.docean.plugin.test.rpc.client;

import lombok.extern.slf4j.Slf4j;
import run.mone.docean.plugin.rpc.anno.RpcService;

/**
 * @author goodjava@qq.com
 * @date 2023/1/8 15:55
 */
@RpcService(interfaceClass = IBizService.class, name = "com.xiaomi.youpin.docean.plugin.test.rpc.client.IBizService")
@Slf4j
public class BizService implements IBizService {

    @Override
    public String hi() {
        return "hi";
    }

    @Override
    public int sum(int a, int b) {
        log.info("call sum");
        return a + b;
    }

}
