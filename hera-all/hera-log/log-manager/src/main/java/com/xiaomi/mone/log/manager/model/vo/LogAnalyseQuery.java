package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogAnalyseQuery implements Serializable {

    private Long spaceId;

    private Long storeId;

}
