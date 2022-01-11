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

package com.xiaomi.mione.mquic.demo.server.manager;

import com.xiaomi.mione.mquic.demo.server.task.ITask;
import com.xiaomi.mione.mquic.demo.server.task.PingTask;
import lombok.Setter;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
 */
@Setter
public class TaskManager {

    private PingTask task = new PingTask();

    private ChannelManager channelManager;

    public void execute() {
        task.setChannelManager(channelManager);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
            task.execute();
        },0,5, TimeUnit.SECONDS);
    }

}
