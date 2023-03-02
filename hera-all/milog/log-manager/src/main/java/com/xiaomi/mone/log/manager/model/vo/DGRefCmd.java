package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DGRefCmd implements Serializable {
    private Long dashboardId;

    private Long graphId;

    private String point;

    private String privateName;
}
