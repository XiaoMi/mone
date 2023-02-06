package com.xiaomi.youpin.docean.plugin.test.rpc.server;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.test.rpc.client.IBizService;
import run.mone.docean.plugin.rpc.anno.RpcReference;

/**
 * @author goodjava@qq.com
 * @date 2023/1/8 15:57
 */
@Service
public class RpcService {

    @RpcReference(interfaceClass = IBizService.class)
    private IBizService bizService;

    public String hi() {
        return bizService.hi();
    }

    public int sum(int a, int b) {
        return bizService.sum(a, b);
    }

}
