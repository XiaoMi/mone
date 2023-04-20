package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class GraphQuery implements Serializable {

    private Long spaceId;

    private Long storeId;

    private String graphName;
}
