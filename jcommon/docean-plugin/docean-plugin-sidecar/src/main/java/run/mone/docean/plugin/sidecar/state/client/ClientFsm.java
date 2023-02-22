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

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.MutableObject;
import com.xiaomi.youpin.docean.common.Safe;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.IClient;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/19
 * 状态机
 */
@Slf4j
@Component
public class ClientFsm {

    @Setter
    private BaseState state = null;

    @Setter
    private IClient client;

    private GlobalState globalState = new GlobalState();

    private long lastExecuteTime = 0;

    private ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(1);

    private Future future;

    public void init() {
        this.state = Ioc.ins().getBean(ConnectState.class);
    }


    public void execute() {
        MutableObject obj = new MutableObject();
        Safe.runAndLog(() -> {
            long now = System.currentTimeMillis();
            if (now - this.lastExecuteTime > 100L) {
                globalState.setClient(client);
                globalState.execute();
                obj.setObj(globalState.isRes());
                state.execute();
                lastExecuteTime = System.currentTimeMillis();
            }
        });
        if ((boolean) obj.getObj()) {
            log.info("fsm exit client:{}", client);
            return;
        }
        future = pool.schedule(() -> ClientFsm.this.execute(), ClientFsm.this.state.delay(), TimeUnit.MILLISECONDS);
    }

    public void change(BaseState state) {
        if (state.equals(this.state)) {
            return;
        }
        log.info("change state:{} -> {}", this.state.getClass().getSimpleName(), state.getClass().getSimpleName());
        this.state.exit();
        this.state = state;
        this.state.enter();
        //这样可以保障更快的执行
        this.state.execute();
    }


}
