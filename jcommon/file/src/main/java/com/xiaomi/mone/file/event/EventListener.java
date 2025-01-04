package com.xiaomi.mone.file.event;

/**
 * @author goodjava@qq.com
 * @date 2023/9/25 14:38
 */
public interface EventListener {

    void onEvent(FileEvent event);

    default void remove(Object fileKey) {
    }

    default void stop() {
    }

}
