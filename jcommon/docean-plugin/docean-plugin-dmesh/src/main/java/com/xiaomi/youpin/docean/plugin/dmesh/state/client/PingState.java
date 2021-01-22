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

import com.google.gson.Gson;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.po.UdsRequest;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.docean.plugin.dmesh.context.ClientContext;
import com.xiaomi.youpin.docean.plugin.dmesh.service.MeshServiceConfig;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 12:05
 * 已经进入了正常的通信状态,发ping 保持连接
 */
@Slf4j
@Component
public class PingState extends BaseState {

    @Resource
    private ClientContext context;

    @Resource
    private ClientFsm fsm;

    @Value("$uds_app")
    private String app;

    @Resource
    private UdsClient client;

    private int errNum = 0;

    @Override
    public void exit() {
        this.errNum = 0;
    }

    @Override
    public void execute() {
        log.info("ping state execute");
        if (context.getState() == 1) {
            fsm.change(Ioc.ins().getBean(ConnectState.class));
        }

        //像服务器发送ping信息
        UdsRequest request = new UdsRequest();
        request.setApp(app);
        request.setCmd("ping");

        boolean err = false;
        try {
            UdsCommand res = client.call(request);
            log.info("{}", res.getData());
        } catch (Throwable ex) {
            err = true;
            log.error(ex.getMessage());
            if (this.errNum++ > 3) {
                fsm.change(Ioc.ins().getBean(ConnectState.class));
            }
        }
        if (err) {
            return;
        }


        //更新服务列表
        List<MeshServiceConfig> list = Ioc.ins().getBean("serviceConfigList");
        if (list.size() > 0) {
            SafeRun.run(() -> {
                UdsClient uc = Ioc.ins().getBean(UdsClient.class);
                UdsCommand req = UdsCommand.createRequest();
                req.setCmd("updateServerList");
                req.setServiceName("updateServerList");
                req.setData(new Gson().toJson(list));
                req.setApp(this.app);
                UdsCommand r = uc.call(req);
                if (null != r) {
                    log.info("update service list success");
                }
            });
        }
    }

    @Override
    public long delay() {
        return 5000L;
    }
}
