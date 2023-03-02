package com.xiaomi.mone.log.manager.model;
import lombok.Data;
import java.io.Serializable;

@Data
public class StatisticsQuery implements Serializable {

    private Long spaceId;
    private Long logstoreId;
    private String tail;
    private Long startTime;
    private Long endTime;

}
