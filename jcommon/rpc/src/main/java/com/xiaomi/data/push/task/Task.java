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

package com.xiaomi.data.push.task;

import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcServer;
import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class Task {

    private Runnable runnable;

    private RpcClient client;

    private RpcServer server;

    private long delay;

    public Task(Runnable runnable, long delay) {
        this.runnable = runnable;
        this.delay = delay;
    }

    public Task() {
    }
}
