package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.service.bo.AppCapacityAutoAdjustBo;

/**
 * @author gaoxihui
 * @date 2022/6/6 7:18 下午
 */
public interface AppCapacityAutoAdjustService {

    public Boolean createOrUpData(AppCapacityAutoAdjustBo appCapacityAutoAdjustBo);
}
