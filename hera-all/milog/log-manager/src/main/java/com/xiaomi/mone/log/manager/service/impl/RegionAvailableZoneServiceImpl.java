package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.manager.dao.MilogRegionAvailableZoneDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogRegionAvailableZoneDO;
import com.xiaomi.mone.log.manager.service.RegionAvailableZoneService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RegionAvailableZoneServiceImpl implements RegionAvailableZoneService {

    private ConcurrentHashMap<String, MilogRegionAvailableZoneDO> map = null;

    @Resource
    private MilogRegionAvailableZoneDao milogRegionAvailableZoneDao;

    public void init(){
        log.info("init Region and Zone");
        List<MilogRegionAvailableZoneDO> milogRegionAvailableZoneDOList = milogRegionAvailableZoneDao.listMilogRegionAvailableZones(null);
        if (milogRegionAvailableZoneDOList != null && !milogRegionAvailableZoneDOList.isEmpty()){
            map = new ConcurrentHashMap<>();
            milogRegionAvailableZoneDOList.forEach(o -> {
                map.put(o.getZoneNameEN(), o);
            });
        }
        log.info("init Region and Zone complete, content:{}", map);
    }

    @Override
    public MilogRegionAvailableZoneDO getRegionAndZone(String zoneEN) {
        if (map == null || map.isEmpty()){
            init();
        }
        return map.get(zoneEN);
    }

    public void clear(){
        map = null;
    }
}
