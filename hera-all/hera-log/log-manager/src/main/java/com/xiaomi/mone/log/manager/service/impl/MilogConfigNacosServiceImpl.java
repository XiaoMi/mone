package com.xiaomi.mone.log.manager.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.alibaba.nacos.api.config.ConfigService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.manager.common.Utils;
import com.xiaomi.mone.log.manager.dao.*;
import com.xiaomi.mone.log.manager.domain.EsCluster;
import com.xiaomi.mone.log.manager.model.pojo.*;
import com.xiaomi.mone.log.manager.service.MilogConfigNacosService;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.nacos.DynamicConfigProvider;
import com.xiaomi.mone.log.manager.service.nacos.DynamicConfigPublisher;
import com.xiaomi.mone.log.manager.service.nacos.FetchStreamMachineService;
import com.xiaomi.mone.log.manager.service.nacos.MultipleNacosConfig;
import com.xiaomi.mone.log.manager.service.nacos.impl.*;
import com.xiaomi.mone.log.model.*;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/19 16:10
 */
@Slf4j
@Service
public class MilogConfigNacosServiceImpl implements MilogConfigNacosService {

    private static Map<String, DynamicConfigPublisher> configPublisherMap = new HashedMap();
    private static Map<String, DynamicConfigProvider> configProviderMap = new HashedMap();

    private static Map<String, FetchStreamMachineService> streamServiceUniqueMap = new HashedMap();

    private StreamConfigNacosPublisher streamConfigNacosPublisher;

    private StreamConfigNacosProvider streamConfigNacosProvider;

    private SpaceConfigNacosPublisher spaceConfigNacosPublisher;

    private SpaceConfigNacosProvider spaceConfigNacosProvider;

    private FetchStreamMachineService fetchStreamMachineService;

    @Resource
    private MilogMachineDao milogMachineDao;

    @Resource
    private MilogLogstoreDao logstoreDao;

    @Resource
    private MilogAppTopicRelDao milogAppTopicRelDao;

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private EsCluster esCluster;

    @Resource
    private MilogSpaceDao milogSpaceDao;

    @Resource
    private MilogAppMiddlewareRelDao milogAppMiddlewareRelDao;
    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;

    @Resource
    private NacosNaming nacosNaming;

    @Resource
    private ConfigService configService;

    @Value(value = "$europe.ip.key")
    private String europeIpKey;

    @Value(value = "$app.env")
    private String appEnv;

    /**
     * 推送namespace配置
     *
     * @param spaceId spaceId
     */

    @Override
    public void publishStreamConfig(Long spaceId, Long tailId, Integer type, Integer projectTypeCode, String motorRoomEn) {
        //1.查询所有的stream的机器Ip--实时查询
        List<String> mioneStreamIpList = fetchStreamMachineService.streamMachineUnique();
        log.info("查询到log-stream的机器列表：{}", new Gson().toJson(mioneStreamIpList));
        //2.发送数据
        streamConfigNacosPublisher.publish(DEFAULT_APP_NAME, dealStreamConfigByRule(mioneStreamIpList, spaceId, type, projectTypeCode));
    }

    private synchronized MiLogStreamConfig dealStreamConfigByRule(List<String> ipList, Long spaceId,
                                                                  Integer type, Integer projectTypeCode) {
        MiLogStreamConfig existConfig = streamConfigNacosProvider.getConfig(DEFAULT_APP_NAME);
        // 新增配置
        if (null == existConfig || OperateEnum.ADD_OPERATE.getCode().equals(type) || OperateEnum.UPDATE_OPERATE.getCode().equals(type)) {
            // 配置还没有配置，初始化配置
            if (null == existConfig) {
                existConfig = new MiLogStreamConfig();
                Map<String, Map<Long, String>> config = new HashMap<>();
                boolean idAdd = false;
                for (String ip : ipList) {
                    Map<Long, String> map = Maps.newHashMapWithExpectedSize(1);
                    if (!idAdd) {
                        map.put(spaceId, CommonExtensionServiceFactory.getCommonExtensionService().getLogManagePrefix() + TAIL_CONFIG_DATA_ID + spaceId);
                        idAdd = true;
                    }
                    config.put(ip, map);
                }
                existConfig.setConfig(config);
            } else {
                Map<String, Map<Long, String>> config = existConfig.getConfig();
                if (config.values().stream()
                        .flatMap(longStringMap -> longStringMap.values().stream())
                        .anyMatch(s -> s.contains(spaceId.toString()))) {
                    return existConfig;
                }
                // 1.先平均数量
                // 2.新增nameSpace
                if (CollectionUtils.isNotEmpty(ipList)) {
                    for (String ip : ipList) {
                        if (!config.containsKey(ip)) {
                            config.put(ip, Maps.newHashMap());
                        }
                    }
                }
                // 每个机器容纳的nameSpace数量
                Map<String, Integer> ipSizeMap = config.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, stringMapEntry -> stringMapEntry.getValue().size()));
                String key = ipSizeMap.entrySet().stream()
                        .filter(entry -> ipList.contains(entry.getKey()))
                        .min(Map.Entry.comparingByValue()).get().getKey();
                config.get(key).put(spaceId, CommonExtensionServiceFactory.getCommonExtensionService().getLogManagePrefix() + TAIL_CONFIG_DATA_ID + spaceId);
            }
        }
        // 删除配置
        if (OperateEnum.DELETE_OPERATE.getCode().equals(type)) {
            if (null != existConfig) {
                Map<String, Map<Long, String>> config = existConfig.getConfig();
                config.values().forEach(longStringMap -> {
                    longStringMap.keySet().removeIf(key -> key.equals(spaceId));
                });
            }
            spaceConfigNacosPublisher.remove(spaceId.toString());
        }
        return existConfig;
    }

    @Override
    public void publishNameSpaceConfig(String motorRoomEn, Long spaceId, Long storeId, Long tailId, Integer type, String changeType) {
        Assert.notNull(spaceId, "logSpaceId not empty");
        Assert.notNull(storeId, "storeId not empty");
        //发送数据
        spaceConfigNacosPublisher.publish(spaceId.toString(),
                dealSpaceConfigByRule(motorRoomEn, spaceId, storeId, tailId, type, changeType));
    }

    /**
     * 选择合适的nacos环境地址
     *
     * @param motorRoomEn
     */
    public void chooseCurrentEnvNacosSerevice(String motorRoomEn) {
        MilogMiddlewareConfig middlewareConfig = milogMiddlewareConfigDao.queryCurrentEnvNacos(motorRoomEn);
        if (null != middlewareConfig) {
            ConfigService configService = MultipleNacosConfig.getConfigService(middlewareConfig.getNameServer());

            spaceConfigNacosPublisher = (SpaceConfigNacosPublisher) configPublisherMap.get(SPACE_PREFIX + motorRoomEn);
            if (null == spaceConfigNacosPublisher) {
                spaceConfigNacosPublisher = new SpaceConfigNacosPublisher();
                spaceConfigNacosPublisher.setConfigService(configService);
                configPublisherMap.put(SPACE_PREFIX + motorRoomEn, spaceConfigNacosPublisher);
            }

            streamConfigNacosPublisher = (StreamConfigNacosPublisher) configPublisherMap.get(STREAM_PREFIX + motorRoomEn);
            if (null == streamConfigNacosPublisher) {
                streamConfigNacosPublisher = new StreamConfigNacosPublisher();
                streamConfigNacosPublisher.setConfigService(configService);
                configPublisherMap.put(STREAM_PREFIX + motorRoomEn, streamConfigNacosPublisher);
            }

            spaceConfigNacosProvider = (SpaceConfigNacosProvider) configProviderMap.get(SPACE_PREFIX + motorRoomEn);
            if (null == spaceConfigNacosProvider) {
                spaceConfigNacosProvider = new SpaceConfigNacosProvider();
                spaceConfigNacosProvider.setConfigService(configService);
                configProviderMap.put(SPACE_PREFIX + motorRoomEn, spaceConfigNacosProvider);
            }

            streamConfigNacosProvider = (StreamConfigNacosProvider) configProviderMap.get(STREAM_PREFIX + motorRoomEn);
            if (null == streamConfigNacosProvider) {
                streamConfigNacosProvider = new StreamConfigNacosProvider();
                streamConfigNacosProvider.setConfigService(configService);
                configProviderMap.put(STREAM_PREFIX + motorRoomEn, streamConfigNacosProvider);
            }
            fetchStreamMachineService = streamServiceUniqueMap.get(STREAM_PREFIX + motorRoomEn);
            if (null == fetchStreamMachineService) {
                NacosNaming nacosNaming = MultipleNacosConfig.getNacosNaming(middlewareConfig.getNameServer());
                fetchStreamMachineService = new NacosFetchStreamMachineService(nacosNaming);
                streamServiceUniqueMap.put(STREAM_PREFIX + motorRoomEn, fetchStreamMachineService);
            }
        } else {
            log.info("当前机房：{}没有nacos配置信息", motorRoomEn);
        }
    }

    @Override
    public void removeStreamConfig(Long id) {
        spaceConfigNacosPublisher.remove(id + "");
    }

    private synchronized MilogSpaceData dealSpaceConfigByRule(
            String motorRoomEn, Long spaceId, Long storeId, Long tailId, Integer type, String changeType) {
        MilogSpaceData existConfig = spaceConfigNacosProvider.getConfig(spaceId.toString());
        // 新增配置
        if (null == existConfig || OperateEnum.ADD_OPERATE.getCode().equals(type)) {
            // 配置还没有配置，初始化配置
            if (null == existConfig || null == existConfig.getSpaceConfig()) {
                existConfig = new MilogSpaceData();
                existConfig.setMilogSpaceId(spaceId);
                List<SinkConfig> spaceConfigs = Lists.newArrayList();
                spaceConfigs.add(assembleSinkConfig(storeId, tailId, motorRoomEn));
                existConfig.setSpaceConfig(spaceConfigs);
            } else {
                List<SinkConfig> spaceConfig = existConfig.getSpaceConfig();
                SinkConfig currentStoreConfig = spaceConfig.stream()
                        .filter(sinkConfig -> sinkConfig.getLogstoreId().equals(storeId))
                        .findFirst()
                        .orElse(null);
                if (null != currentStoreConfig) {
                    List<LogtailConfig> logtailConfigs = currentStoreConfig.getLogtailConfigs();
                    if (CollectionUtils.isEmpty(logtailConfigs)) {
                        logtailConfigs = Lists.newArrayList();
                    }
                    logtailConfigs.add(assembleLogTailConfigs(tailId));
                    currentStoreConfig.setLogtailConfigs(logtailConfigs);
                } else {
                    //新加入的logstore
                    spaceConfig.add(assembleSinkConfig(storeId, tailId, motorRoomEn));
                }
            }
        }
        // 删除配置--删除log-tail
        if (OperateEnum.DELETE_OPERATE.getCode().equals(type) && !LOG_STORE.equalsIgnoreCase(changeType)) {
            if (null != existConfig) {
                List<SinkConfig> spaceConfig = existConfig.getSpaceConfig();
                SinkConfig currentStoreConfig = spaceConfig.stream()
                        .filter(sinkConfig -> sinkConfig.getLogstoreId().equals(storeId))
                        .findFirst()
                        .orElse(null);
                if (null != currentStoreConfig) {
                    List<LogtailConfig> logtailConfigs = currentStoreConfig.getLogtailConfigs();
                    logtailConfigs.removeIf(logtailConfig -> logtailConfig.getLogtailId().equals(tailId));
                }
            }
        }
        // 删除log-store
        if (OperateEnum.DELETE_OPERATE.getCode().equals(type) && LOG_STORE.equalsIgnoreCase(changeType)) {
            if (null != existConfig) {
                List<SinkConfig> sinkConfigListDelStore = existConfig.getSpaceConfig().stream()
                        .filter(sinkConfig -> !storeId.equals(sinkConfig.getLogstoreId()))
                        .collect(Collectors.toList());
                existConfig.setSpaceConfig(sinkConfigListDelStore);
            }
        }
        // 修改配置--找到这个store下特定的tail进行修改
        if (OperateEnum.UPDATE_OPERATE.getCode().equals(type)) {
            if (null != existConfig) {
                List<SinkConfig> spaceConfig = existConfig.getSpaceConfig();
                //比较store是否变化
                SinkConfig newSinkConfig = assembleSinkConfig(storeId, tailId, motorRoomEn);
                SinkConfig currentStoreConfig = spaceConfig.stream()
                        .filter(sinkConfig -> sinkConfig.getLogstoreId().equals(storeId))
                        .findFirst()
                        .orElse(null);
                if (null != currentStoreConfig) {
                    if (!newSinkConfig.equals(currentStoreConfig)) {
                        currentStoreConfig.updateStoreParam(newSinkConfig);
                    }
                    // 找到旧store下的特定的tail
                    LogtailConfig filterLogTailConfig = currentStoreConfig.getLogtailConfigs().stream()
                            .filter(logTailConfig -> Objects.equals(tailId, logTailConfig.getLogtailId()))
                            .findFirst()
                            .orElse(null);
                    if (null != filterLogTailConfig) {
                        BeanUtil.copyProperties(assembleLogTailConfigs(tailId), filterLogTailConfig);
                    } else {
                        log.info("query logtailConfig no designed config,tailId:{},insert", tailId);
                        currentStoreConfig.getLogtailConfigs().add(assembleLogTailConfigs(tailId));
                    }
                } else {
                    //不存在，新增
                    //新加入的logstore
                    spaceConfig.add(assembleSinkConfig(storeId, tailId, motorRoomEn));
                }
            }
        }
        return existConfig;
    }

    public SinkConfig assembleSinkConfig(Long storeId, Long tailId, String motorRoomEn) {
        SinkConfig sinkConfig = new SinkConfig();
        sinkConfig.setLogstoreId(storeId);
        // 查询logStore
        MilogLogStoreDO milogLogstore = logstoreDao.queryById(storeId);
        if (null != milogLogstore) {
            sinkConfig.setLogstoreName(milogLogstore.getLogstoreName());
            sinkConfig.setKeyList(Utils.parse2KeyAndTypeList(milogLogstore.getKeyList(), milogLogstore.getColumnTypeList()));
            MilogEsClusterDO esInfo = esCluster.getById(milogLogstore.getEsClusterId());
            sinkConfig.setEsIndex(milogLogstore.getEsIndex());
            sinkConfig.setEsInfo(buildEsInfo(esInfo));
        }
        sinkConfig.setLogtailConfigs(Arrays.asList(assembleLogTailConfigs(tailId)));
        return sinkConfig;
    }

    private EsInfo buildEsInfo(MilogEsClusterDO clusterDO) {
        if (Objects.equals(ES_CONWAY_PWD, clusterDO.getConWay())) {
            return new EsInfo(clusterDO.getId(), clusterDO.getAddr(), clusterDO.getUser(), clusterDO.getPwd());
        }
        return new EsInfo(clusterDO.getId(), clusterDO.getAddr(), clusterDO.getToken(), clusterDO.getDtCatalog(), clusterDO.getDtDatabase());
    }


    public LogtailConfig assembleLogTailConfigs(Long tailId) {
        LogtailConfig logtailConfig = new LogtailConfig();
        MilogLogTailDo milogLogTail = milogLogtailDao.queryById(tailId);
        if (null != milogLogTail) {
            logtailConfig.setLogtailId(tailId);
            logtailConfig.setTail(milogLogTail.getTail());
            logtailConfig.setParseType(milogLogTail.getParseType());
            logtailConfig.setParseScript(milogLogTail.getParseScript());
            logtailConfig.setValueList(milogLogTail.getValueList());
            logtailConfig.setAppType(milogLogTail.getAppType());
            // 查询mq信息
            handleTailConfig(tailId, milogLogTail.getStoreId(), milogLogTail.getSpaceId(),
                    milogLogTail.getMilogAppId(), logtailConfig, milogLogTail.getAppType());
        }
        return logtailConfig;
    }

    private void handleTailConfig(Long tailId, Long storeId, Long spaceId, Long milogAppId, LogtailConfig logtailConfig, Integer appType) {
        List<MilogAppMiddlewareRel> milogAppMiddlewareRels = milogAppMiddlewareRelDao.queryByCondition(milogAppId, null, tailId);
        if (CollectionUtils.isNotEmpty(milogAppMiddlewareRels)) {
            MilogAppMiddlewareRel milogAppMiddlewareRel = milogAppMiddlewareRels.get(0);
            MilogAppMiddlewareRel.Config config = milogAppMiddlewareRel.getConfig();
            MilogMiddlewareConfig middlewareConfig = milogMiddlewareConfigDao.queryById(milogAppMiddlewareRel.getMiddlewareId());

            logtailConfig.setAk(middlewareConfig.getAk());
            logtailConfig.setSk(middlewareConfig.getSk());
            logtailConfig.setTopic(config.getTopic());
            String tag = Utils.createTag(spaceId, storeId, tailId);

            logtailConfig.setTag(tag);
            logtailConfig.setConsumerGroup(config.getConsumerGroup());
            if (MiddlewareEnum.ROCKETMQ.getCode().equals(middlewareConfig.getType())) {
                logtailConfig.setType(MiddlewareEnum.ROCKETMQ.getName());
                logtailConfig.setClusterInfo(middlewareConfig.getNameServer());
            }
        }
    }
}
