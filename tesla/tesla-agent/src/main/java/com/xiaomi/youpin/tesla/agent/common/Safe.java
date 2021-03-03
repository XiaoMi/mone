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

package com.xiaomi.youpin.tesla.agent.common;

import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * 安全执行器
 */
@Slf4j
public abstract class Safe {

    public interface ThrowableRunnable {
        void run() throws Throwable;
    }

    public static void execute(ThrowableRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error("safe execute error:" + ex.getMessage(), ex);
        }
    }

}
