package com.xiaomi.mone.file.event;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/9/25 14:37
 */
@Data
@Builder
public class FileEvent {

    private EventType type;

    private String fileName;

    private Object fileKey;

    private long pointer;


}
