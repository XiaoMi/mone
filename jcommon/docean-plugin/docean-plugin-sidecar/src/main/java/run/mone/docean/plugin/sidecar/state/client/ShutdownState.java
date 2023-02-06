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

package run.mone.docean.plugin.sidecar.state.client;

import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.IClient;
import run.mone.docean.plugin.sidecar.bo.SideCarApp;
import run.mone.docean.plugin.sidecar.service.SideCarInfoService;

import javax.annotation.Resource;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/19
 */
@Component
@Slf4j
public class ShutdownState extends BaseState {


    @Resource
    private ClientFsm fsm;

    @Value("$app")
    private String app;


    @Override
    public void execute() {
        log.info("side car client:{} init state", app);
        sendMsg(Ioc.ins());
    }


    private void sendMsg(Ioc ioc) {
        IClient client = ioc.getBean("sideCarClient");
        UdsCommand req = UdsCommand.createRequest();
        req.setCmd("unRegSideCar");
        SideCarApp app = new SideCarApp();
        SideCarInfoService service = ioc.getBean("sideCarInfoService", null);
        if (null != service) {
            app = service.getSideCarApp();
        }
        app.setApp(this.app);
        req.setData(app);
        req.setApp(this.app);
        client.call(req);
    }

}
