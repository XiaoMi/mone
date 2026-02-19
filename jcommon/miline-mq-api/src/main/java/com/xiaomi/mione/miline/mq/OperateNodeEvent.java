package com.xiaomi.mione.miline.mq;

import com.xiaomi.mione.miline.mq.enums.EventSourceEnum;
import com.xiaomi.mione.miline.mq.enums.EventTagEnum;
import com.xiaomi.mione.miline.mq.enums.EventTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 节点操作事件
 *
 * @author qoder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperateNodeEvent<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * mq消息的事件tag， 消费这可通过tag指定订阅的消息
     */
    private EventTagEnum eventTag;
    /**
     * 事件来源
     */
    private EventSourceEnum source;
    /**
     * 事件类型
     */
    private EventTypeEnum eventType;

    /**
     * 事件消息体
     */
    private T eventBody;

    public String getEventTag() {
        return eventTag.getTag();
    }

    public EventTagEnum getEventTagEnum() {
        return eventTag;
    }
}
