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

import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.Safe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 12:10
 * 状态机
 */
@Slf4j
@Component
public class ClientFsm {

    private BaseState state = null;

    private long lastExecuteTime = 0;

    private ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(1);

    private Future future;

    public void init() {
        this.state = Ioc.ins().getBean(ConnectState.class);
    }


    public void execute() {
        Safe.runAndLog(() -> {
            long now = System.currentTimeMillis();
            if (now - this.lastExecuteTime > 100L) {
                state.execute();
                lastExecuteTime = System.currentTimeMillis();
            }
        });
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
