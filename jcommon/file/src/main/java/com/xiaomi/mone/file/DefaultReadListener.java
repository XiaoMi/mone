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

package com.xiaomi.mone.file;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/24 14:33
 */
public class DefaultReadListener implements ReadListener {

    private Consumer<ReadEvent> consumer;

    public DefaultReadListener(Consumer<ReadEvent> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onEvent(ReadEvent event) {
        consumer.accept(event);
    }

    @Override
    public boolean isContinue(String line) {
        if (null == line) {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
