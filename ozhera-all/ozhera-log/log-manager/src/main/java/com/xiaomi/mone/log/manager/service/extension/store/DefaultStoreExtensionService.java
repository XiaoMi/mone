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
package com.xiaomi.mone.log.manager.service.extension.store;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.vo.ResourceUserSimple;
import com.xiaomi.mone.log.manager.common.ManagerConstant;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
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
 * @description Store resource binding
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

    @Resource
    private MilogLogstoreDao logStoreDao;

    @Override
    public boolean storeInfoCheck(LogStoreParam param) {
        return false;
    }

    @Override
    public void storeResourceBinding(MilogLogStoreDO ml, LogStoreParam cmd, OperateEnum operateEnum) {
        if (StringUtils.isNotEmpty(ml.getEsIndex()) && null != ml.getMqResourceId() && null != ml.getEsClusterId()) {
            return;
        }
        ResourceUserSimple resourceUserConfig = resourceConfigService.userResourceList(cmd.getMachineRoom(), cmd.getLogType());
        if (resourceUserConfig.getInitializedFlag()) {
            //Select the ES cluster
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

    @Override
    public void deleteStorePostProcessing(MilogLogStoreDO logStoreD) {

    }

    @Override
    public String getMangerEsLabel() {
        return ManagerConstant.ES_LABEL;
    }

    @Override
    public boolean updateLogStore(MilogLogStoreDO ml) {
        return logStoreDao.updateMilogLogStore(ml);
    }

    @Override
    public boolean isNeedSendMsgType(Integer logType) {
        return true;
    }
}
