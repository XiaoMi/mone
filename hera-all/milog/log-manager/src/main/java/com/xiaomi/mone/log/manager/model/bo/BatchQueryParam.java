package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/19 11:32
 */
@Data
public class BatchQueryParam {
    private List<Long> ids;
}
