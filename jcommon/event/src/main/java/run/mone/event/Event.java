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

package run.mone.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import java.util.concurrent.Executors;

/**
 * @author goodjava@qq.com
 * @date 2022/5/16
 */
public class Event {

    private EventBus eventBus;

    private Event() {
        eventBus = new AsyncEventBus("default", Executors.newFixedThreadPool(5));
    }

    private static final class LazyHolder {
        private static final Event ins = new Event();
    }

    public static final Event ins() {
        return LazyHolder.ins;
    }

    public void register(Object listener) {
        eventBus.register(listener);
    }

    public void post(Object event) {
        eventBus.post(event);
    }


}
