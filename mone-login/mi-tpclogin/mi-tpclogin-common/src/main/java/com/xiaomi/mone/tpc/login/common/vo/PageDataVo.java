package com.xiaomi.mone.tpc.login.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:47
 */
@ToString
@Data
public class PageDataVo<T> implements Serializable {
    private boolean pager;
    private int page;
    private int pageSize;
    private int total;
    private List<T> list;
}
