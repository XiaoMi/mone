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

package run.mone.docean.plugin.sidecar.manager;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import run.mone.docean.plugin.sidecar.state.client.ClientFsm;
import run.mone.docean.plugin.sidecar.state.client.ConnectState;
import run.mone.docean.plugin.sidecar.state.client.ShutdownState;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2022/6/19
 */
@Service
@Slf4j
public class ShutdownHookManager {

    @Resource
    private ClientFsm clientFsm;

    @Value("$shutdownHook")
    private String shutdownHook;

    public void init() {
        if ("true".equals(shutdownHook) || "".equals(shutdownHook)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> Ioc.ins().destory()));
        }
    }


    public void destory() {
        if (null != clientFsm) {
            clientFsm.change(Ioc.ins().getBean(ShutdownState.class));
        }
    }


}
