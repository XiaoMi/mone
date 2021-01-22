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

package com.xiaomi.youpin.docean.plugin.dmesh.state.client;

import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.context.UdsClientContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.po.UdsRequest;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 12:02
 * 连接服务器状态
 */
@Component
@Slf4j
public class ConnectState extends BaseState {

    @Resource
    private UdsClient client;

    @Value("$uds_app")
    private String app;

    @Resource
    private ClientFsm fsm;


    @Override
    public void execute() {
        log.info("client connect state");
        try {
            Channel channel = client.getChannel();
            if (null != channel) {
                UdsRequest request = new UdsRequest();
                request.setApp(app);
                request.setCmd("ping");
                UdsClientContext.ins().channel.set(channel);
                UdsCommand res = client.call(request);
                if (null != res) {
                    fsm.change(Ioc.ins().getBean(InitState.class));
                }
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public long delay() {
        return 500L;
    }
}
