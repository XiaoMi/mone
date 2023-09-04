/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.rpc.processor.service;

import com.google.common.util.concurrent.RateLimiter;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.Date;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/25 16:59
 */
public class LimitTest {


    @SneakyThrows
    @Test
    public void testLimiter() {
        final RateLimiter rateLimiter = RateLimiter.create(3);
        new Thread(() -> {
            while (true) {
                rateLimiter.acquire();
                System.out.println(new Date());
            }
        }).start();
        System.in.read();
    }

    @Test
    public void testIf() {
        int num = 1;
        if (num == 1) {
            System.out.println("急急急");
        } else if (1 == 1) {
            System.out.println("梵蒂冈");
        }
    }

}
