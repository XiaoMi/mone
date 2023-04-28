package com.xiaomi.mone.log.manager.dao;

import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.service.extension.resource.ResourceExtensionServiceFactory;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.collections.CollectionUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.EQUAL_OPERATE;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/22 12:03
 */
@Service
public class MilogMiddlewareConfigDao {

    @Resource
    private NutDao dao;

    public List<MilogMiddlewareConfig> queryMiddlewareConfigByCondition(Condition cnd, Pager pager) {
        if (null == pager) {
            return dao.query(MilogMiddlewareConfig.class, cnd);
        }
        return dao.query(MilogMiddlewareConfig.class, cnd, pager);
    }

    public Integer queryMiddlewareConfigCountByCondition(Condition cnd) {
        return dao.count(MilogMiddlewareConfig.class, cnd);
    }

    public void addMiddlewareConfig(MilogMiddlewareConfig addParamToAddEntity) {
        dao.insert(addParamToAddEntity);
    }

    public MilogMiddlewareConfig queryById(Long id) {
        return dao.fetch(MilogMiddlewareConfig.class, id);
    }

    public void updateMiddlewareConfig(MilogMiddlewareConfig middlewareConfig) {
        dao.update(middlewareConfig);
    }

    public void deleteMiddlewareConfig(Long id) {
        dao.delete(MilogMiddlewareConfig.class, id);
    }

    public List<MilogMiddlewareConfig> queryCurrentMontorRoomMQ(String montorRoomEn) {
        List<MilogMiddlewareConfig> middlewareConfigs = dao.query(MilogMiddlewareConfig.class, Cnd.where("region_en", EQUAL_OPERATE, montorRoomEn.trim()));
        if (CollectionUtils.isNotEmpty(middlewareConfigs)) {
            return middlewareConfigs.stream().filter(middlewareConfig -> MiddlewareEnum.ROCKETMQ.getCode().equals(middlewareConfig.getType())).collect(Collectors.toList());
        }
        return null;
    }

    public MilogMiddlewareConfig queryDefaultMiddlewareConfig() {
        List<MilogMiddlewareConfig> defaultConfigList = dao.query(MilogMiddlewareConfig.class, Cnd.where("region_en", EQUAL_OPERATE, MachineRegionEnum.CN_MACHINE.getEn()).and("is_default", EQUAL_OPERATE, 1));
        if (CollectionUtils.isNotEmpty(defaultConfigList)) {
            return defaultConfigList.stream().filter(middlewareConfig -> MiddlewareEnum.ROCKETMQ.getCode().equals(middlewareConfig.getType())).findFirst().orElse(null);
        }
        return null;
    }

    public MilogMiddlewareConfig queryCurrentEnvNacos(String enName) {
        Cnd cnd = Cnd.where("type", EQUAL_OPERATE, MiddlewareEnum.NCOS.getCode()).and("region_en", EQUAL_OPERATE, enName);
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = dao.query(MilogMiddlewareConfig.class, cnd);
        if (CollectionUtils.isNotEmpty(milogMiddlewareConfigs)) {
            return milogMiddlewareConfigs.get(milogMiddlewareConfigs.size() - 1);
        }
        return null;
    }

    public List<MilogMiddlewareConfig> queryAll() {
        return dao.query(MilogMiddlewareConfig.class, null);
    }

    public MilogMiddlewareConfig queryDefaultMqMiddlewareConfigMotorRoom(String motorRooman) {
        Cnd cnd = Cnd.where("type", "in", ResourceExtensionServiceFactory.getResourceExtensionService().getResourceCode())
                .and("region_en", EQUAL_OPERATE, motorRooman);
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = dao.query(MilogMiddlewareConfig.class, cnd);
        if (CollectionUtils.isNotEmpty(milogMiddlewareConfigs) && milogMiddlewareConfigs.size() == 1) {
            return milogMiddlewareConfigs.get(milogMiddlewareConfigs.size() - 1);
        }
        if (CollectionUtils.isNotEmpty(milogMiddlewareConfigs) && milogMiddlewareConfigs.size() > 1) {
            return milogMiddlewareConfigs.stream().filter(milogMiddlewareConfig -> Constant.YES.intValue() == milogMiddlewareConfig.getIsDefault().intValue()).findFirst().get();
        }
        return null;
    }

    public MilogMiddlewareConfig queryNacosRegionByNameServer(String nameServer) {
        Cnd cnd = Cnd.where("type", EQUAL_OPERATE, MiddlewareEnum.NCOS.getCode()).and("name_server", EQUAL_OPERATE, nameServer);
        List<MilogMiddlewareConfig> milogMiddlewareConfigs = dao.query(MilogMiddlewareConfig.class, cnd);
        if (CollectionUtils.isNotEmpty(milogMiddlewareConfigs)) {
            return milogMiddlewareConfigs.get(milogMiddlewareConfigs.size() - 1);
        }
        return null;
    }

    public List<MilogMiddlewareConfig> queryByResourceCode(Integer resourceCode, String regionCode) {
        Cnd cnd = Cnd.where("type", EQUAL_OPERATE, resourceCode);
        cnd.and("region_en", EQUAL_OPERATE, regionCode);
        cnd.orderBy("utime", "desc");
        return dao.query(MilogMiddlewareConfig.class, cnd);
    }

    public List<MilogMiddlewareConfig> selectByAlias(String alias) {
        Cnd cnd = Cnd.where("alias", EQUAL_OPERATE, alias);
        return dao.query(MilogMiddlewareConfig.class, cnd);
    }
}
