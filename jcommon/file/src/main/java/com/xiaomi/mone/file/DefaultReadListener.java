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
