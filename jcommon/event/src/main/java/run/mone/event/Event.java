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
