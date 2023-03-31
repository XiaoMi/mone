package com.xiaomi.mone.log.manager.service.init_sql;

import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.dao.MilogMiddlewareConfigDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;

import javax.annotation.Resource;
import java.util.Objects;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_OPERATOR;

/**
 * @author wtt
 * @version 1.0
 * @description 初始化默认的nacos配置信息到表中
 * @date 2023/3/3 10:45
 */
@Service
public class NcosConfigSqlService extends BaseService {
    private static final String DEFAULT_NCOS_ALIAS = "系统nacos";

    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;

    @Value(value = "$defaultNacosAddres")
    private String defaultNacosAddress;

    public void init() {
        String defaultRegionCode = MachineRegionEnum.CN_MACHINE.getEn();
        MilogMiddlewareConfig middlewareConfig = milogMiddlewareConfigDao.queryCurrentEnvNacos(defaultRegionCode);
        if (null == middlewareConfig) {
            addNcosConfig(defaultRegionCode);
            return;
        }
        if (Objects.equals(middlewareConfig.getNameServer(), defaultNacosAddress)) {
            updateNcosAddress(middlewareConfig);
        }
    }

    private void addNcosConfig(String defaultRegionCode) {
        MilogMiddlewareConfig middlewareConfig;
        middlewareConfig = new MilogMiddlewareConfig();
        middlewareConfig.setType(MiddlewareEnum.NCOS.getCode());
        middlewareConfig.setRegionEn(defaultRegionCode);
        middlewareConfig.setAlias(DEFAULT_NCOS_ALIAS);
        middlewareConfig.setNameServer(defaultNacosAddress);
        middlewareConfig.setIsDefault(Constant.YES.intValue());
        wrapBaseCommon(middlewareConfig, OperateEnum.ADD_OPERATE, DEFAULT_OPERATOR);
        milogMiddlewareConfigDao.addMiddlewareConfig(middlewareConfig);
    }

    private void updateNcosAddress(MilogMiddlewareConfig middlewareConfig) {
        middlewareConfig.setNameServer(defaultNacosAddress);
        wrapBaseCommon(middlewareConfig, OperateEnum.UPDATE_OPERATE, DEFAULT_OPERATOR);
        milogMiddlewareConfigDao.updateMiddlewareConfig(middlewareConfig);
    }

}
