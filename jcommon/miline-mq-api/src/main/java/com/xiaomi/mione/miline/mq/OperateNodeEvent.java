package com.xiaomi.mione.miline.mq;

import com.xiaomi.mione.miline.mq.enums.EventSourceEnum;
import com.xiaomi.mione.miline.mq.enums.OperateNodeEventTypeEnum;
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
public class OperateNodeEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件来源
     */
    private EventSourceEnum source;

    /**
     * 事件类型
     */
    private OperateNodeEventTypeEnum eventType;

    /**
     * 事件消息体
     */
    private OperateNodeEventBody eventBody;
}
