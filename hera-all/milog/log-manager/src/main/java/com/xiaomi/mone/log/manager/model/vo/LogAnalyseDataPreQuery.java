package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogAnalyseDataPreQuery implements Serializable {

    private Long storeId;

    private String fieldName;

    private Integer typeCode;

    private Long startTime;

    private Long endTime;

}
