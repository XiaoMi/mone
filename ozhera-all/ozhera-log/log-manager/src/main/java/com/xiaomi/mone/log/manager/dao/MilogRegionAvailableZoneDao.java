/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.dao;

import com.xiaomi.mone.log.manager.model.pojo.MilogRegionAvailableZoneDO;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangping17
 * @date 2021-10-15 Region，AZ query
 */
@Service
@Slf4j
public class MilogRegionAvailableZoneDao {

    @Resource
    private NutDao dao;

    /**
     * According to zone English name or region and zone information
     *
     * @param zoneNameENs
     * @return
     */
    public List<MilogRegionAvailableZoneDO> listMilogRegionAvailableZones(List<Long> zoneNameENs) {
        List<MilogRegionAvailableZoneDO> milogRegionAvailableZoneDOList = null;
        Cnd cnd = null;
        if (zoneNameENs != null && !zoneNameENs.isEmpty()) {
            cnd = Cnd.where("zone_name_en", "in", zoneNameENs);
        }
        milogRegionAvailableZoneDOList = dao.query(MilogRegionAvailableZoneDO.class, cnd);
        return milogRegionAvailableZoneDOList;
    }

    public void insert(MilogRegionAvailableZoneDO milogRegionAvailableZoneDO) {
        dao.insert(milogRegionAvailableZoneDO);
    }


}
