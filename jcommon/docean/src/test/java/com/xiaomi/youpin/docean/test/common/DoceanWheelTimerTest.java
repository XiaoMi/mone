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

package com.xiaomi.youpin.docean.test.common;

import com.xiaomi.youpin.docean.common.DoceanWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2/16/21
 */
public class DoceanWheelTimerTest {

    @Test
    public void testnewTimeout() throws IOException {
        DoceanWheelTimer timer = new DoceanWheelTimer();
        timer.init();
        System.out.println(new Date());
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println(new Date());
            }
        }, 5, TimeUnit.SECONDS);

        System.in.read();
    }
}
