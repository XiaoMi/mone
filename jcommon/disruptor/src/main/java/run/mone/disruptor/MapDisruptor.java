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

package run.mone.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2020/6/7
 */
public class MapDisruptor {

    private Disruptor<MapEvent> disruptor;

    public boolean start(Consumer<MapEvent> consumer, int bufferSize) {
        try {
            EventFactory<MapEvent> eventFactory = () -> new MapEvent();
            ExecutorService executor = Executors.newCachedThreadPool();
            int ringBufferSize = bufferSize;
            disruptor = new Disruptor<MapEvent>(eventFactory,
                    ringBufferSize, executor, ProducerType.MULTI,
                    new YieldingWaitStrategy());
            disruptor.handleEventsWith((event, sequence, endOfBatch) -> consumer.accept(event));
            disruptor.start();
            return true;
        } catch (Throwable ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public Map<String, Object> get(Consumer<Map<String, Object>> consumer) {
        MutableObject mo = new MutableObject();
        disruptor.publishEvent((mapEvent, sequence) -> {
            consumer.accept(mapEvent.getData());
            mo.setObj(sequence);
        });
        return disruptor.getRingBuffer().get(mo.getObj()).getData();
    }


    public void publishEvent(Consumer<Map<String, Object>> consumer) {
        try {
            this.disruptor.publishEvent((mapEvent, sequence) -> {
                consumer.accept(mapEvent.getData());
            });
        } catch (Throwable ex) {
            System.err.println(ex.getMessage());
        }
    }


}
