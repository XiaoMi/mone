package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateDashboardCmd implements Serializable {

    private String name;

    private Long spaceId;

    private Long storeId;
}
