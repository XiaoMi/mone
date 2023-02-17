package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogAnalyseDataQuery implements Serializable {

    private Long graphId;

    private Long startTime;

    private Long endTime;

}
