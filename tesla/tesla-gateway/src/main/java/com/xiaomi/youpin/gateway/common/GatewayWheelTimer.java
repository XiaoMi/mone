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

package com.xiaomi.youpin.gateway.common;

import io.netty.util.HashedWheelTimer;
import io.netty.util.TimerTask;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2/16/21
 */
@Service
public class GatewayWheelTimer {

    private HashedWheelTimer hashedWheelTimer;

    @PostConstruct
    public void init() {
        hashedWheelTimer = new HashedWheelTimer(1000, TimeUnit.MILLISECONDS, 16);
    }


    public void newTimeout(TimerTask task, long delay, TimeUnit timeUnit) {
        hashedWheelTimer.newTimeout(task, delay, timeUnit);
    }

    public void newTimeout(Runnable runnable, long delay) {
        hashedWheelTimer.newTimeout(timeout -> runnable.run(), delay, TimeUnit.MILLISECONDS);
    }
}