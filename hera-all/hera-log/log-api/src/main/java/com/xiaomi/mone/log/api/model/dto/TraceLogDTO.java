package com.xiaomi.mone.log.api.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

@Data
public class TraceLogDTO implements Serializable {
    private Set<String> dataList;

    public TraceLogDTO(Set<String> dataList) {
        this.dataList = dataList;
    }

    public static TraceLogDTO emptyData() {
        return new TraceLogDTO(new TreeSet<>());
    }
}
