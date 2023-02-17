package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.manager.model.pojo.MilogRegionAvailableZoneDO;

/**
 * @author zhangping17
 * @date 2021-10-18
 */
public interface RegionAvailableZoneService {

    /**
     * 根据zone英文名获取region和zone
     * @param zoneEN
     * @return
     */
    MilogRegionAvailableZoneDO getRegionAndZone(String zoneEN);
}
