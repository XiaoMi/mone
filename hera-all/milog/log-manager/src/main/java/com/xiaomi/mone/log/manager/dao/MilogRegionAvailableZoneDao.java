package com.xiaomi.mone.log.manager.dao;

import com.xiaomi.mone.log.manager.model.pojo.MilogRegionAvailableZoneDO;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangping17
 * @date 2021-10-15 Region，AZ查询
 */
@Service
@Slf4j
public class MilogRegionAvailableZoneDao {

    @Resource
    private NutDao dao;

    /**
     * 根据zone英文名或者region和zone信息
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
