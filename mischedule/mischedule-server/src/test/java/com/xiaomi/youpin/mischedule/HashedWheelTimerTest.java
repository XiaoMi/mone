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

package com.xiaomi.youpin.mischedule;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import org.junit.Test;

import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HashedWheelTimerTest {

    @Test
    public void testHashedWheelTimer() throws InterruptedException {
        HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(1000, TimeUnit.MILLISECONDS, 2);
        System.out.println(new Date() + " submitted");
        Timeout timeout = hashedWheelTimer.newTimeout((t) -> {
            System.out.println(new Date() + " executed");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(new Date() + " FINISH");
        }, 10, TimeUnit.SECONDS);


        Thread.currentThread().join();

    }
}
