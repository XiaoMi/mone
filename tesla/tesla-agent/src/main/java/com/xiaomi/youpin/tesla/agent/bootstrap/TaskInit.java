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

package com.xiaomi.youpin.tesla.agent.bootstrap;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.agent.interceptor.Log;
import com.xiaomi.youpin.tesla.agent.service.IService;
import com.xiaomi.youpin.tesla.agent.task.CleanTask;
import com.xiaomi.youpin.tesla.agent.task.PingTask;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Component(desc = "schedule service")
@Slf4j
public class TaskInit implements IService {

    @Resource
    private RpcClient client;

    public TaskInit(RpcClient client) {
        this.client = client;
    }

    public TaskInit() {
    }

    @Log
    @Override
    public void init() {
        client.setTasks(Lists.newArrayList(
                new PingTask(client),
                new CleanTask()
        ));
    }
}
