package com.xiaomi.mone.log.agent.export;

import com.xiaomi.mone.log.agent.channel.Closeable;
import com.xiaomi.mone.log.api.model.msg.LineMessage;

import java.util.List;

/**
 * @author shanwb
 * @date 2021-07-19
 */
public interface MsgExporter extends Closeable {
    int BATCH_EXPORT_SIZE = 200;

    /**
     * 单条export
     * @param message
     */
    void export(LineMessage message);

    /**
     * 多条export
     * @param messageList
     */
    void export(List<LineMessage> messageList);

    /**
     * 批量export条数
     * @return
     */
    default int batchExportSize() {
        return BATCH_EXPORT_SIZE;
    }
}
