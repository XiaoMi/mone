package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class LogQuery implements Serializable {
    private String logstore;
    private String tail;
    private Long startTime;
    private Long endTime;
    private Integer pageSize;
    private Object[] beginSortValue;
    private String fullTextSearch;
    private String sortKey = "timestamp";
    private Boolean asc = false;
    private Long[] appIds;

    public LogQuery() {
    }

    public LogQuery(String logstore, String tail, Long startTime, Long endTime, String fullTextSearch, String sortKey) {
        this.logstore = logstore;
        this.tail = tail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fullTextSearch = fullTextSearch;
        this.sortKey = sortKey;
    }

    public LogQuery(String logstore, String tail, Long startTime, Long endTime, String fullTextSearch, String sortKey, Long[] appIds) {
        this.logstore = logstore;
        this.tail = tail;
        this.startTime = startTime;
        this.endTime = endTime;
        this.fullTextSearch = fullTextSearch;
        this.sortKey = sortKey;
        this.appIds = appIds;
    }
}
