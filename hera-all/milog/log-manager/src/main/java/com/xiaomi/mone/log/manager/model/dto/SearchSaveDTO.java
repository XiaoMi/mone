package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class SearchSaveDTO {
    private String id;
    private String name;
    private String param;
    private Long startTime;
    private Long endTime;
    private Integer isFixTime;
    private String common;
}
