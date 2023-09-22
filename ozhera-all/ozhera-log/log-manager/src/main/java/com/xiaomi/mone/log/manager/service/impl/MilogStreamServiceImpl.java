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

import com.alibaba.nacos.api.config.ConfigService;
import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogMachineDao;
import com.xiaomi.mone.log.manager.dao.MilogMiddlewareConfigDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.service.MilogStreamService;
import com.xiaomi.mone.log.manager.service.nacos.MultipleNacosConfig;
import com.xiaomi.mone.log.manager.service.nacos.impl.SpaceConfigNacosProvider;
import com.xiaomi.mone.log.manager.service.nacos.impl.SpaceConfigNacosPublisher;
import com.xiaomi.mone.log.manager.service.nacos.impl.StreamConfigNacosProvider;
import com.xiaomi.mone.log.manager.service.nacos.impl.StreamConfigNacosPublisher;
import com.xiaomi.mone.log.model.LogtailConfig;
import com.xiaomi.mone.log.model.MiLogStreamConfig;
import com.xiaomi.mone.log.model.MilogSpaceData;
import com.xiaomi.mone.log.model.SinkConfig;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_APP_NAME;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/27 17:25
 */
@Service
public class MilogStreamServiceImpl implements MilogStreamService {

    private StreamConfigNacosProvider streamConfigNacosProvider;

    private StreamConfigNacosPublisher streamConfigNacosPublisher;

    private SpaceConfigNacosProvider spaceConfigNacosProvider;

    private SpaceConfigNacosPublisher spaceConfigNacosPublisher;
    @Resource
    private MilogMachineDao milogMachineDao;
    @Resource
    private MilogLogstoreDao milogLogstoreDao;
    @Resource
    private MilogLogTailDao milogLogtailDao;
    @Resource
    private MilogConfigNacosServiceImpl milogConfigNacosService;

    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;

    @Value("$log_type_mq_not_consume")
    private String logTypeMqNotConsume;

    /**
     * @param ip
     * @return
     */
    @Override
    public Result<String> configIssueStream(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return Result.failParam("IP cannot be empty");
        }
        streamConfigNacosProvider = new StreamConfigNacosProvider();
        spaceConfigNacosProvider = new SpaceConfigNacosProvider();
        spaceConfigNacosPublisher = new SpaceConfigNacosPublisher();
        for (String address : MultipleNacosConfig.getAllNachosAdders()) {
            ConfigService configService = MultipleNacosConfig.getConfigService(address);
            streamConfigNacosProvider.setConfigService(configService);
            spaceConfigNacosProvider.setConfigService(configService);
            spaceConfigNacosPublisher.setConfigService(configService);
            MilogMiddlewareConfig milogMiddlewareConfig = milogMiddlewareConfigDao.queryNacosRegionByNameServer(address.trim());
            if (null != milogMiddlewareConfig) {
                MiLogStreamConfig existConfig = streamConfigNacosProvider.getConfig(DEFAULT_APP_NAME);
                Optional.ofNullable(existConfig).map(miLogStreamConfig -> {
                    Map<String, Map<Long, String>> config = existConfig.getConfig();
                    config.entrySet().stream().filter(entry -> entry.getKey().equals(ip)).forEach(entry -> {
                        Map<Long, String> streamMap = entry.getValue();
                        streamMap.keySet().stream().forEach(spaceKey -> {
                            MilogSpaceData milogSpaceData = spaceConfigNacosProvider.getConfig(spaceKey.toString());
                            if (null == milogSpaceData) {
                                milogSpaceData = new MilogSpaceData();
                            }
                            //Find all configurations under this space
                            List<SinkConfig> sinkConfigs = generateSinkConfig(spaceKey, milogMiddlewareConfig.getRegionEn());
                            milogSpaceData.setSpaceConfig(sinkConfigs);
                            spaceConfigNacosPublisher.publish(spaceKey.toString(), milogSpaceData);
                        });
                    });
                    return null;
                }).orElse(false);
            }
        }
        return Result.success("success");
    }

    private List<SinkConfig> generateSinkConfig(Long spaceKey, String region) {
        List<SinkConfig> sinkConfigs = Lists.newArrayList();
        List<MilogLogStoreDO> logstoreDOS = milogLogstoreDao.getMilogLogstoreBySpaceIdRegion(spaceKey, region.trim());
        logstoreDOS = logstoreDOS.stream().filter(milogLogstoreDO -> !milogLogstoreDO.getLogType().toString().equals(logTypeMqNotConsume)).collect(Collectors.toList());
        logstoreDOS.forEach(milogLogstoreDO -> {
            List<SinkConfig> sameStoreSinkConfigs = Lists.newArrayList();
            List<LogtailConfig> logtailConfigs = Lists.newArrayList();
            List<MilogLogTailDo> logtailDos = milogLogtailDao.getMilogLogtailByStoreId(milogLogstoreDO.getId());
            for (MilogLogTailDo logtailDo : logtailDos) {
                SinkConfig sinkConfig = milogConfigNacosService.assembleSinkConfig(milogLogstoreDO.getId(), logtailDo.getId(), MachineRegionEnum.CN_MACHINE.getEn());
                sameStoreSinkConfigs.add(sinkConfig);
            }
            // merge
            sameStoreSinkConfigs.stream().forEach(sinkConfig -> {
                logtailConfigs.addAll(sinkConfig.getLogtailConfigs());
            });
            if (CollectionUtils.isNotEmpty(sameStoreSinkConfigs)) {
                SinkConfig sameStoresinkConfig = sameStoreSinkConfigs.get(0);
                sameStoresinkConfig.setLogtailConfigs(logtailConfigs);
                sinkConfigs.add(sameStoresinkConfig);
            }
        });
        return sinkConfigs;
    }

    @Override
    public void executeSql(String sql) {
        milogMachineDao.executeSql(sql);
    }


}
