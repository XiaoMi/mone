package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class UpdateGraphCmd implements Serializable {

    private Long id;

    private String name;

    private String fieldName;

    private Integer graphType;

}
