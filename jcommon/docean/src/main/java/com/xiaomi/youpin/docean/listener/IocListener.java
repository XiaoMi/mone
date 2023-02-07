package com.xiaomi.youpin.docean.listener;

import com.xiaomi.youpin.docean.listener.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author goodjava@qq.com
 */
public class IocListener {

    private List<Listener> listenerList = new ArrayList<>();

    private ExecutorService pool = Executors.newFixedThreadPool(2);

    public void regListener(Listener listener) {
        listenerList.add(listener);
    }


    public void onEvent(Event event) {
        listenerList.stream().forEach(listener -> {
            listener.onEvent(event);
        });
    }

    public void multicastEvent(Event event) {
        if (event.isAsync()) {
            pool.submit(() -> onEvent(event));
        } else {
            onEvent(event);
        }
    }

}
