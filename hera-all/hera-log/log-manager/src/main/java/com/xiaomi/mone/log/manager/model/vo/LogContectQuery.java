package com.xiaomi.mone.log.manager.model.vo;
import lombok.Data;

import java.io.Serializable;

@Data
public class LogContectQuery implements Serializable {

    private String logstore;
    private String tail;
    private Integer pageSize;
    private Object[] beginSortValue;
    private String logip;
    private Integer sort; //1 正序 2 倒序
}
