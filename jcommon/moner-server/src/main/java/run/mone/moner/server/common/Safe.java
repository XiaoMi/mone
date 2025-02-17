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

package run.mone.moner.server.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/2 11:28
 */
@Slf4j
public class Safe {


    public static void run(ExRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static void run(ExRunnable runnable, Consumer<Throwable> consumer) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            consumer.accept(ex);
        }
    }

    public static void runAndIgnore(ExRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.warn("ignore run error: {}", ex.getMessage());
        }
    }

    public static <T> T call(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static <T> T callAndIgnore(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Throwable ex) {
            log.warn("ignore call error:{}", ex.getMessage());
            return null;
        }
    }


    public interface ExRunnable {
        void run() throws Throwable;
    }

}
