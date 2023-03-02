package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

import java.util.List;

@Data
public class Pagination {
    int page;
    int pageSize;
    long total;
    List<AlertLogBo> list;
}