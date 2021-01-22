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

package com.xiaomi.youpin.docean.common;

import com.xiaomi.youpin.docean.exception.DoceanException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Slf4j
public class Safe {


    public interface ExRunnable {
        void run() throws Throwable;
    }


    public static void run(ExRunnable runnable, Consumer<Throwable> consumer) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            consumer.accept(ex);
        }
    }

    public static void run(ExRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            throw new DoceanException(ex);
        }
    }

}
