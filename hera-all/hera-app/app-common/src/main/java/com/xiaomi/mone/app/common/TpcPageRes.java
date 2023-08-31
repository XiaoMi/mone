package com.xiaomi.mone.app.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description tpc分页信息返回值
 * @date 2023/8/16 16:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TpcPageRes<T> {
    private boolean pager;
    private int page;
    private int pageSize;
    private int total;
    private List<T> list;
}
