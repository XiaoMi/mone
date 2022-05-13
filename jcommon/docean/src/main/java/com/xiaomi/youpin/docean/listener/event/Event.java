package com.xiaomi.youpin.docean.listener.event;

import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class Event {

    private EventType eventType;

    private Object data;

    private boolean async = false;

    public Event(EventType eventType) {
        this.eventType = eventType;
    }

    public Event(EventType eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public <T> T getData() {
        return (T)data;
    }
}
