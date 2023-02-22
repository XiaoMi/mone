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

import com.google.gson.Gson;
import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.IClient;
import run.mone.docean.plugin.sidecar.bo.Ping;
import run.mone.docean.plugin.sidecar.service.SideCarInfoService;

import javax.annotation.Resource;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/19
 * 已经进入了正常的通信状态,发ping 保持连接
 */
@Slf4j
@Component
public class PingState extends BaseState {

    @Setter
    @Resource
    private ClientFsm fsm;

    @Setter
    @Value("$app")
    private String app;

    @Setter
    @Value(value = "$disable_log", defaultValue = "false")
    private String disableLog;

    @Setter
    @Resource(name = "sideCarClient")
    private IClient client;

    @Setter
    private ConnectState connectState;

    private int errNum = 0;

    public static final long TIME = 5000L;

    @Override
    public void exit() {
        this.errNum = 0;
    }

    private void log(String messsage, Object... params) {
        if (disableLog.equals("false")) {
            log.info(messsage, params);
        }
    }

    @Override
    public void execute() {
        log("side car client:{} ping state", app);
        //像服务器发送ping信息
        UdsCommand request = UdsCommand.createRequest();
        request.setApp(app);
        request.setCmd("ping");
        String ping = "ping";
        SideCarInfoService service = Ioc.ins().getBean("sideCarInfoService", null);
        if (null != service) {
            Ping p = service.getPingData();
            ping = new Gson().toJson(p);
        }
        request.setData(ping);
        try {
            RpcCommand res = client.call(request);
            if (null != service) {
                service.consumerPingRes(new String(res.getData()));
            }
            log("{}", new String(res.getData()));
            this.errNum = 0;
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            if (this.errNum++ > 3) {
                if (null != this.connectState) {
                    fsm.change(this.connectState);
                } else {
                    fsm.change(Ioc.ins().getBean(ConnectState.class));
                }
            }
        }
    }

    @Override
    public long delay() {
        return TIME;
    }
}
