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

import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.IClient;

import javax.annotation.Resource;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/19
 * 连接服务器状态
 */
@Component
@Slf4j
public class ConnectState extends BaseState {

    @Setter
    @Resource(name = "sideCarClient")
    private IClient client;

    @Setter
    @Value("$app")
    private String app;

    @Setter
    @Resource
    private ClientFsm fsm;

    /**
     * 支持非单例模式下的调用
     */
    @Setter
    private InitState initState;


    @Override
    public void execute() {
        log.info("side car client:{} connect state", app);
        try {
            UdsCommand request = UdsCommand.createRequest();
            request.setApp(app);
            request.setCmd("ping");
            request.setData("ping");
            RpcCommand res = client.call(request);
            if (null != res) {
                if (null != this.initState) {
                    fsm.change(this.initState);
                } else {
                    fsm.change(Ioc.ins().getBean(InitState.class));
                }
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public long delay() {
        return 1000L;
    }
}
