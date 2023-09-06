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
package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.dao.MilogAppMiddlewareRelDao;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.dao.MilogMiddlewareConfigDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogAppMiddlewareRel;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.service.MilogAppMiddlewareRelService;
import com.xiaomi.mone.log.manager.service.MqConfigService;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_CONSUMER_GROUP;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/23 11:14
 */
@Service
@Slf4j
public class MilogAppMiddlewareRelServiceImpl implements MilogAppMiddlewareRelService {

    @Resource
    private MilogAppMiddlewareRelDao milogAppMiddlewareRelDao;
    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;
    @Resource
    private HeraAppServiceImpl heraAppService;
    @Resource
    private MilogLogTailDao milogLogtailDao;

    private MqConfigService mqConfigService;


    @Override
    public void bindingTailConfigRel(Long tailId, Long milogAppId, Long middlewareConfigId, String topicName) {
        //1.Query the configuration configuration configuration
        //2.Storage
        MilogMiddlewareConfig wareConfig = queryMiddlewareConfig(middlewareConfigId);
        if (null != wareConfig) {
            middlewareConfigId = wareConfig.getId();
            instantiateMqConfigSubClass(MiddlewareEnum.queryByCode(wareConfig.getType()));
            AppBaseInfo appBaseInfo = heraAppService.queryById(milogAppId);
            MilogAppMiddlewareRel.Config config = new MilogAppMiddlewareRel.Config();
            if (StringUtils.isEmpty(topicName)) {
                config = mqConfigService.generateConfig(wareConfig.getAk(),
                        wareConfig.getSk(), wareConfig.getNameServer(), wareConfig.getServiceUrl(),
                        wareConfig.getAuthorization(), wareConfig.getOrgId(), wareConfig.getTeamId(), Long.valueOf(appBaseInfo.getBindId()), appBaseInfo.getAppName(), appBaseInfo.getPlatformName(), tailId);
            } else {
                config.setTopic(topicName);
                config.setPartitionCnt(1);
            }
            MilogLogTailDo milogLogtailDo = milogLogtailDao.queryById(tailId);
            String tag = Utils.createTag(milogLogtailDo.getSpaceId(), milogLogtailDo.getStoreId(), milogLogtailDo.getId());
            config.setTag(tag);
            config.setConsumerGroup(DEFAULT_CONSUMER_GROUP + tag);
            milogAppMiddlewareRelDao.insertUpdate(generateMiddlewareRel(tailId, milogAppId, middlewareConfigId, config));
        } else {
            log.error("The middleware configuration for the current environment is empty,tailId:{},milogAppId:{},middlewareConfigId:{}", tailId, milogAppId, middlewareConfigId);
        }
    }

    @Override
    public void defaultBindingAppTailConfigRel(Long id, Long milogAppId,
                                               Long middleWareId, String topicName, Integer batchSendSize) {
        MilogLogTailDo logTailDo = milogLogtailDao.queryById(id);
        MilogMiddlewareConfig wareConfig = milogMiddlewareConfigDao.queryById(middleWareId);
        if (null != wareConfig) {
            if (StringUtils.isEmpty(topicName)) {
                List<String> commonTagTopicNames = Utils.generateCommonTagTopicName(StringUtils.EMPTY);
                topicName = commonTagTopicNames.get(Utils.getRandomNum(commonTagTopicNames.size()));
            }
            MilogAppMiddlewareRel.Config config = new MilogAppMiddlewareRel.Config();
            config.setTopic(topicName);
            config.setPartitionCnt(1);
            String tag = Utils.createTag(logTailDo.getSpaceId(), logTailDo.getStoreId(), id);
            config.setConsumerGroup(String.format("%s_%s", "group", tag));
            config.setTag(tag);
            if (null != batchSendSize) {
                config.setBatchSendSize(batchSendSize);
            }
            handleTailMqRel(id, milogAppId, wareConfig, config);
        } else {
            log.error("If the current organization does not have MQ configuration information configured, configure the message configuration information of the current organization,tailId：{},storeId:{},middleWareId:{}",
                    id, milogAppId, middleWareId);
            throw new MilogManageException("If the current organization does not configure MQ configuration information, configure the resource configuration information of the current department");
        }
    }

    private void handleTailMqRel(Long tailId, Long milogAppId, MilogMiddlewareConfig wareConfig, MilogAppMiddlewareRel.Config config) {
        MilogLogTailDo milogLogtailDo = milogLogtailDao.queryById(tailId);
        String tag = Utils.createTag(milogLogtailDo.getSpaceId(), milogLogtailDo.getStoreId(), milogLogtailDo.getId());
        config.setTag(tag);
        config.setConsumerGroup(DEFAULT_CONSUMER_GROUP + tag);
        MilogAppMiddlewareRel milogAppMiddlewareRel = generateMiddlewareRel(tailId, milogAppId, wareConfig.getId(), config);
        milogAppMiddlewareRelDao.insertUpdate(milogAppMiddlewareRel);
    }

    private MilogAppMiddlewareRel generateMiddlewareRel(Long tailId, Long milogAppId, Long configId, MilogAppMiddlewareRel.Config config) {
        MilogAppMiddlewareRel milogAppMiddlewareRel = new MilogAppMiddlewareRel();
        milogAppMiddlewareRel.setMilogAppId(milogAppId);
        milogAppMiddlewareRel.setMiddlewareId(configId);
        milogAppMiddlewareRel.setTailId(tailId);
        milogAppMiddlewareRel.setConfig(config);
        milogAppMiddlewareRel.setCtime(Instant.now().toEpochMilli());
        milogAppMiddlewareRel.setUtime(Instant.now().toEpochMilli());
        if (StringUtils.isEmpty(MoneUserContext.getCurrentUser().getUser())) {
            MilogLogTailDo milogLogTailDo = milogLogtailDao.queryById(tailId);
            milogAppMiddlewareRel.setCreator(milogLogTailDo.getCreator());
            milogAppMiddlewareRel.setUpdater(milogLogTailDo.getUpdater());
        } else {
            milogAppMiddlewareRel.setCreator(MoneUserContext.getCurrentUser().getUser());
            milogAppMiddlewareRel.setUpdater(MoneUserContext.getCurrentUser().getUser());
        }
        return milogAppMiddlewareRel;
    }

    /**
     * middlewareConfigId does not look for its own correspondence for empty Otherwise, select the default
     *
     * @param middlewareConfigId
     * @return
     */
    public MilogMiddlewareConfig queryMiddlewareConfig(Long middlewareConfigId) {
        MilogMiddlewareConfig wareConfig;
        if (null != middlewareConfigId) {
            wareConfig = milogMiddlewareConfigDao.queryById(middlewareConfigId);
        } else {
            wareConfig = milogMiddlewareConfigDao.queryDefaultMiddlewareConfig();
        }
        return wareConfig;
    }


    private void instantiateMqConfigSubClass(MiddlewareEnum middlewareEnum) {
        switch (middlewareEnum) {
            case ROCKETMQ:
                mqConfigService = Ioc.ins().getBean(RocketMqConfigService.class);
                break;
        }
    }
}
