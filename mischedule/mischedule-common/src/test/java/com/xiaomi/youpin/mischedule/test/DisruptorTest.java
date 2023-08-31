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

package com.xiaomi.youpin.mischedule.test;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Data;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class DisruptorTest {

    @Data
    class Event {
        private int data;
    }

    public class TestEventHandler implements com.lmax.disruptor.EventHandler<Event> {

        private CountDownLatch latch;

        public TestEventHandler(CountDownLatch latch) {
            this.latch = latch;
        }

        public void onEvent(Event event, long sequence, boolean endOfBatch) throws InterruptedException {
            this.latch.countDown();
            System.out.println("Event: " + event + ":" + Thread.currentThread().getId());
//            TimeUnit.SECONDS.sleep(2);
        }
    }

    public class MyEventHandler implements com.lmax.disruptor.EventHandler<Event> {

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) throws Exception {
            System.out.println("---->" + event.getData());
            TimeUnit.SECONDS.sleep(2);
        }
    }

    public class LongEventFactory implements EventFactory<Event> {
        public Event newInstance() {
            System.out.println("new event");
            return new Event();
        }
    }


    @Test
    public void testDisruptor() throws InterruptedException {
        EventFactory<Event> eventFactory = new LongEventFactory();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int ringBufferSize = 1024 * 2; // RingBuffer 大小，必须是 2 的 N 次方；

        Disruptor<Event> disruptor = new Disruptor<Event>(eventFactory,
                ringBufferSize, executor, ProducerType.MULTI,
                new YieldingWaitStrategy());

        int n = 2000;
        CountDownLatch latch = new CountDownLatch(n);

        //可以有多个处理器
        disruptor.handleEventsWith(new TestEventHandler(latch)).then(new MyEventHandler());

        disruptor.start();

        IntStream.range(0, n).parallel().forEach(it -> {
            disruptor.publishEvent((event, sequence) -> {
                System.out.println("push");
                event.setData(it);
            });
        });

        latch.await();


    }

}
