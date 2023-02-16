package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DashboardGraphDTO implements Serializable {

    private Long graphId;

    private String graphPrivateName;

    private String graphName;

    private Integer graphType;

    private String point;

}
