package com.xiaomi.mone.log.model;

import lombok.Data;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 封装 同步到nacos的milog space 配置
 * key:spaceID
 */
@Data
public class MilogSpaceData {
    private Long milogSpaceId;
    private List<SinkConfig> spaceConfig;
}