package com.xiaomi.mone.log.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class MiLogStreamConfig {
    /**
     * 用于维护logstream 实例管理的logSpace
     * key: logstream 实例ip
     */
    private Map<String, Map<Long, String>> config = new ConcurrentHashMap<>();
}
