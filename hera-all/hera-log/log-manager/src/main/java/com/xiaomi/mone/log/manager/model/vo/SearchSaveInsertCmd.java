package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

@Data
public class SearchSaveInsertCmd {
    private Long spaceId;
    private Long storeId;
    /**
     * 多个以,分割
     */
    private String tailId;
    private String name;
    private String queryText;
    private Long startTime;
    private Long endTime;
    private Integer isFixTime;
    private String common;
    private Integer sort;
}
