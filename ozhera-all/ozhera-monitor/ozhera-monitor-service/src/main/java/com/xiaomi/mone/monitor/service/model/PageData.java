package com.xiaomi.mone.monitor.service.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/8/13 11:31 上午
 */
@ToString
@Data
public class PageData<T> implements Serializable {
    Integer page;
    Integer pageSize;
    Long total;
    Map summary;
    T list;
}
