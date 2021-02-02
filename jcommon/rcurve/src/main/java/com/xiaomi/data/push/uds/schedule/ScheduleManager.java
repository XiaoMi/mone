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

package com.xiaomi.data.push.uds.schedule;

import com.xiaomi.data.push.common.SafeRun;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 1/8/21
 */
public class ScheduleManager {

    public static void scheduleAtFixedRate(Runnable runnable, int period) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            SafeRun.run(() -> {
                runnable.run();
            });
        }, 0, period, TimeUnit.SECONDS);
    }

}
