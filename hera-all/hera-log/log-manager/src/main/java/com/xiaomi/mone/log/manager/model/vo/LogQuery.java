/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Data
public class LogQuery implements Serializable {
    private String logstore;
    private Long storeId;
    private String tail;
    private Long tailId;
    private Long startTime;
    private Long endTime;
    private Integer pageSize;
    private Integer page;
    private Object[] beginSortValue;
    private String fullTextSearch;
    private String sortKey = "timestamp";
    private Boolean asc = false;
    private Long[] appIds;

    public LogQuery() {
    }

    public LogQuery(String logstore, Long storeId, String tail, Long startTime, Long endTime, String fullTextSearch, String sortKey) {
        this.logstore = logstore;
        this.storeId = storeId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogQuery logQuery = (LogQuery) o;
        return Objects.equals(logstore, logQuery.logstore) && Objects.equals(tail, logQuery.tail) && Objects.equals(startTime, logQuery.startTime) && Objects.equals(endTime, logQuery.endTime) && Objects.equals(pageSize, logQuery.pageSize) && Arrays.equals(beginSortValue, logQuery.beginSortValue) && Objects.equals(fullTextSearch, logQuery.fullTextSearch) && Objects.equals(sortKey, logQuery.sortKey) && Objects.equals(asc, logQuery.asc) && Arrays.equals(appIds, logQuery.appIds);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(logstore, tail, startTime, endTime, pageSize, fullTextSearch, sortKey, asc);
        result = 31 * result + Arrays.hashCode(beginSortValue);
        result = 31 * result + Arrays.hashCode(appIds);
        return result;
    }
}
