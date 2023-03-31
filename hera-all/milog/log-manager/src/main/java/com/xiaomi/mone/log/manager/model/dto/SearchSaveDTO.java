package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class SearchSaveDTO {
    private String id;
    private String name;
    private Long spaceId;
    private String spaceName;
    private Long storeId;
    private String storeName;
    private String tailId;
    private String tailName;
    private String appName;
    private String queryText;
    private Integer isFixTime;
    private Long startTime;
    private Long endTime;
    private Long sort;
    private Integer orderNum;
    private String common;
}
