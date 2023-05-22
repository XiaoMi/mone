package com.xiaomi.youpin.prometheus.agent.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@Data
public class PageDataVo<T> implements Serializable {
    private Integer pageNo;
    private Integer pageSize;
    private Integer total;
    private List<T> list;
}
