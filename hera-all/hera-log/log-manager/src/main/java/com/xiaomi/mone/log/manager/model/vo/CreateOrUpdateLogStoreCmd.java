package com.xiaomi.mone.log.manager.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdateLogStoreCmd {
    private Long id;
    private Long spaceId;
    private String logstoreName;
    private Integer storePeriod;
    private Integer shardCnt;
    private String keyList;
    private String columnTypeList;
    /**
     * 1. 服务应用日志
     */
    private Integer logType;
    private String esIndex;
    private String machineRoom;
    /**
     * mq资源ID
     */
    private Long mqResourceId;
    /**
     * es资源ID
     */
    private Long esResourceId;
}
