package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateGraphCmd implements Serializable {

    private String name;

    private String fieldName;

    private Long spaceId;

    private Long storeId;

    private Integer graphType;

}
