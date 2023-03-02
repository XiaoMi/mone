package com.xiaomi.mone.log.manager.domain;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaomi.mone.log.api.model.vo.ResourceUserSimple;
import com.xiaomi.mone.log.manager.mapper.MilogEsClusterMapper;
import com.xiaomi.mone.log.manager.model.dto.EsInfoDTO;
import com.xiaomi.mone.log.manager.model.pojo.LogEsClusterDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.model.vo.CreateOrUpdateLogStoreCmd;
import com.xiaomi.mone.log.manager.service.impl.MilogMiddlewareConfigServiceImpl;
import com.xiaomi.youpin.docean.anno.Service;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LogStore {
    @Resource
    private MilogMiddlewareConfigServiceImpl resourceConfigService;

    @Resource
    private MilogMiddlewareConfigServiceImpl milogMiddlewareConfigService;

    @Resource
    private MilogEsClusterMapper milogEsClusterMapper;

    @Resource
    private EsIndexTemplate esIndexTemplate;

    /**
     * es资源绑定
     */
    public void storeResourceBinding(MilogLogStoreDO ml, CreateOrUpdateLogStoreCmd cmd) {
        ResourceUserSimple resourceUserConfig = resourceConfigService.userResourceList(cmd.getMachineRoom(), cmd.getLogType());
        if (resourceUserConfig.getInitializedFlag()) {
            //选择es集群
            if (null == cmd.getEsResourceId()) {
                List<LogEsClusterDO> esClusterDOS = milogEsClusterMapper.selectList(Wrappers.lambdaQuery());
                cmd.setEsResourceId(esClusterDOS.get(esClusterDOS.size() - 1).getId());
            }
            EsInfoDTO esInfo = esIndexTemplate.getEsInfo(cmd.getEsResourceId(), cmd.getLogType(), null);
            cmd.setEsIndex(esInfo.getIndex());
            ml.setEsClusterId(esInfo.getClusterId());
            if (StringUtils.isEmpty(cmd.getEsIndex())) {
                ml.setEsIndex(esInfo.getIndex());
            } else {
                ml.setEsIndex(cmd.getEsIndex());
            }
            if (null == cmd.getMqResourceId()) {
                MilogMiddlewareConfig milogMiddlewareConfig = milogMiddlewareConfigService.queryMiddlewareConfigDefault(cmd.getMachineRoom());
                ml.setMqResourceId(milogMiddlewareConfig.getId());
                cmd.setMqResourceId(milogMiddlewareConfig.getId());
            }
        }
    }
}
