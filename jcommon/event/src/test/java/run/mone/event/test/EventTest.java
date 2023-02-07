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

package run.mone.event.test;

import com.google.common.eventbus.Subscribe;
import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.event.Event;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2022/5/16
 */
public class EventTest {

    @SneakyThrows
    @Test
    public void testEvent() {
        Event.ins().register(new Listener());
        Event.ins().post("abc");
        Event.ins().post("def");
        System.out.println("finish");
        Thread.currentThread().join();
    }

    class Listener {

        @SneakyThrows
        @Subscribe
        public void lisen(String str) {
            TimeUnit.SECONDS.sleep(3);
            System.out.println(Thread.currentThread().getName());
            System.out.println(str);
        }

    }
}
