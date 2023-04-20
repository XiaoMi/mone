package com.xiaomi.mone.log.manager.service.extension.store;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.vo.ResourceUserSimple;
import com.xiaomi.mone.log.manager.domain.EsIndexTemplate;
import com.xiaomi.mone.log.manager.mapper.MilogEsClusterMapper;
import com.xiaomi.mone.log.manager.model.dto.EsInfoDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogEsClusterDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.model.vo.LogStoreParam;
import com.xiaomi.mone.log.manager.service.impl.MilogMiddlewareConfigServiceImpl;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.xiaomi.mone.log.manager.service.extension.store.StoreExtensionService.DEFAULT_STORE_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description store资源绑定
 * @date 2023/4/10 16:19
 */
@Service(name = DEFAULT_STORE_EXTENSION_SERVICE_KEY)
@Slf4j
public class DefaultStoreExtensionService implements StoreExtensionService {

    @Resource
    private MilogMiddlewareConfigServiceImpl resourceConfigService;

    @Resource
    private MilogMiddlewareConfigServiceImpl milogMiddlewareConfigService;

    @Resource
    private MilogEsClusterMapper milogEsClusterMapper;

    @Resource
    private EsIndexTemplate esIndexTemplate;

    @Override
    public void storeResourceBinding(MilogLogStoreDO ml, LogStoreParam cmd, OperateEnum operateEnum) {
        if (StringUtils.isNotEmpty(ml.getEsIndex()) && null != ml.getMqResourceId() && null != ml.getEsClusterId()) {
            return;
        }
        ResourceUserSimple resourceUserConfig = resourceConfigService.userResourceList(cmd.getMachineRoom(), cmd.getLogType());
        if (resourceUserConfig.getInitializedFlag()) {
            //选择es集群
            if (null == cmd.getEsResourceId()) {
                List<MilogEsClusterDO> esClusterDOS = milogEsClusterMapper.selectList(Wrappers.lambdaQuery());
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

    @Override
    public void postProcessing(MilogLogStoreDO ml, LogStoreParam cmd) {
    }

    @Override
    public boolean sendConfigSwitch(LogStoreParam param) {
        return true;
    }
}
