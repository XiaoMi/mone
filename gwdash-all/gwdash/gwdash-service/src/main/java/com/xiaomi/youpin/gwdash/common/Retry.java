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

package com.xiaomi.youpin.gwdash.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2020/7/22
 */
@Slf4j
public abstract class Retry {


    public static boolean run(Supplier<Boolean> supplier, int num) {
        return run(supplier, num, 1);
    }

    public static boolean run(Supplier<Boolean> supplier, int num, int sleepTime) {
        return IntStream.range(0, num).mapToObj(it -> {
            try {
                Boolean res = supplier.get();
                if (!res) {
                    sleep(sleepTime);
                }
                return res;
            } catch (Throwable ex) {
                log.error(ex.getMessage());
                sleep(sleepTime);
                return false;
            }
        }).filter(it -> it).findAny().isPresent();
    }

    private static void sleep(long sleepTime) {
        try {
            TimeUnit.SECONDS.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
