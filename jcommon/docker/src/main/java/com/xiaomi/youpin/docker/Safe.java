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

package com.xiaomi.youpin.docker;

import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2020/7/22
 */
@Slf4j
public abstract class Safe {

    public static void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

}
