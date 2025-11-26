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

package com.xiaomi.youpin.tesla.file.server.service;

import lombok.Data;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Data
public class ScheduleService {

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

    private final List<Runnable> tasks;

    public ScheduleService(List<Runnable> tasks) {
        this.tasks = tasks;
    }

    public void run() {
        tasks.stream().forEach(task -> {
            pool.scheduleAtFixedRate(task, 0, 1000, TimeUnit.SECONDS);
        });
    }

}
