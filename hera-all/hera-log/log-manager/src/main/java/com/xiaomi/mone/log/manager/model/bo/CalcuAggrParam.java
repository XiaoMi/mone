package com.xiaomi.mone.log.manager.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalcuAggrParam {

    private Integer graphType;

    private String graphParam;

    private String bead;

    private Long startTime;

    private Long endTime;

}
