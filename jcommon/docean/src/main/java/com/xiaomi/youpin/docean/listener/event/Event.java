package com.xiaomi.youpin.docean.listener.event;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Data
public class Event {

    private String iocName;

    private EventType eventType;

    private Object data;

    private boolean async = false;

    private Map<String, Object> attachments = new HashMap<>();

    public Event(EventType eventType) {
        this.eventType = eventType;
    }

    public Event(EventType eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public Event(EventType eventType, Object data, Map<String, Object> attachments) {
        this.eventType = eventType;
        this.data = data;
        this.attachments.putAll(attachments);
    }

    public <T> T getData() {
        return (T) data;
    }
}
