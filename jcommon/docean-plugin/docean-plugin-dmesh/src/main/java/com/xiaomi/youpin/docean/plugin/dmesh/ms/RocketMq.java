package com.xiaomi.youpin.docean.plugin.dmesh.ms;

import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;

/**
 * @author goodjava@qq.com
 * @date 1/10/21
 */
@MeshMsService(interfaceClass = RocketMq.class, name = "rocketmq")
public interface RocketMq {

    void send(String app, String message);

}
