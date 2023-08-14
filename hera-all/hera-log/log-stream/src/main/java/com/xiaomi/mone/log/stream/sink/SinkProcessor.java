package com.xiaomi.mone.log.stream.sink;

import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/7 14:21
 */
public interface SinkProcessor {
    boolean execute(Map<String, Object> map);
}
