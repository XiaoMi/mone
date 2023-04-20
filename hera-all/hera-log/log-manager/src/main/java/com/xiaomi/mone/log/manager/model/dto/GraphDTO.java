package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GraphDTO implements Serializable {

    private String name;

    private String fieldName;

    private String graphType;
}
