package com.xiaomi.mone.log.stream.config;

import com.alibaba.nacos.api.config.listener.Listener;
import com.google.gson.Gson;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.common.NetUtils;
import com.xiaomi.mone.log.model.MiLogStreamConfig;
import com.xiaomi.mone.log.model.MilogSpaceData;
import com.xiaomi.mone.log.stream.exception.StreamException;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.NamedThreadFactory;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.nacos.NacosConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.*;

@Service
@Slf4j
public class ConfigManager {
    @Resource
    private NacosConfig nacosConfig;

    final String spaceDataId = LOG_MANAGE_PREFIX + NAMESPACE_CONFIG_DATA_ID;

    /**
     * 存储ConfigManager所管理的MilogSpaceConfigListener
     * key: spaceId
     * value: MilogSpaceConfigListener
     */
    @Getter
    private ConcurrentHashMap<Long, MilogConfigListener> listeners = new ConcurrentHashMap<>();

    /**
     * 本实例所需要监听的MilogSpaceData
     * key: spaceId
     * value: milogSpaceData
     */
    private ConcurrentHashMap<Long, MilogSpaceData> milogSpaceDataMap = new ConcurrentHashMap<>();


    private final ExecutorService pool = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1), new NamedThreadFactory("milog-nacos-config-update", true),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private Gson gson = new Gson();

    /**
     * 服务启动时执行一次
     *
     * @throws StreamException
     */
    public void initStream() throws StreamException {
        log.debug("[initStream} nacos dataId:{},group:{}", spaceDataId, DEFAULT_GROUP_ID);
        String streamConfigStr = nacosConfig.getConfigStr(spaceDataId, DEFAULT_GROUP_ID, DEFAULT_TIME_OUT_MS);
        MiLogStreamConfig milogStreamConfig = null;
        try {
            if (StringUtils.isNotEmpty(streamConfigStr) && !NULLVALUE.equals(streamConfigStr)) {
                milogStreamConfig = new Gson().fromJson(streamConfigStr, MiLogStreamConfig.class);
            } else {
                log.warn("[ConfigManager.initConfigManager] Nacos configuration [dataID:{},group:{}]not found,exit initConfigManager", spaceDataId, DEFAULT_GROUP_ID);
                return;
            }
            String localIp = getLocalIp();
            Map<String, Map<Long, String>> config = milogStreamConfig.getConfig();
            if (config.containsKey(localIp)) {
                Map<Long, String> milogStreamDataMap = config.get(localIp);
                log.info("[ConfigManager.initConfigManager] localIP:{},data:{}", localIp, gson.toJson(milogStreamDataMap));
                for (Long spaceId : milogStreamDataMap.keySet()) {
                    final String dataId = milogStreamDataMap.get(spaceId);
                    // init spaceData config
                    String milogSpaceDataStr = nacosConfig.getConfigStr(dataId, DEFAULT_GROUP_ID, DEFAULT_TIME_OUT_MS);
                    if (StringUtils.isNotEmpty(milogSpaceDataStr)) {
                        MilogSpaceData milogSpaceData = new Gson().fromJson(milogSpaceDataStr, MilogSpaceData.class);
                        if (null != milogSpaceData) {
                            milogSpaceDataMap.put(spaceId, milogSpaceData);
                        }
                    }
                    MilogSpaceData milogSpaceData = milogSpaceDataMap.get(spaceId);
                    if (null != milogSpaceData) {
                        MilogConfigListener configListener = new MilogConfigListener(spaceId, dataId, DEFAULT_GROUP_ID, milogSpaceData, nacosConfig);
                        configListener.OnMessage();
                        addListener(spaceId, configListener);
                    }
                    log.info("[ConfigManager.initStream] added log config listener for spaceId:{},dataId:{}", spaceId, dataId);
                }
            } else {
                log.info("server start current not contain space config,currentIP:{}", localIp);
            }
        } catch (Exception e) {
            log.error("[ConfigManager.initStream] initStream exec err", e);
        }
    }

    private String getLocalIp() {
        String localIp = Config.ins().get("localIp", "");
        String tmpLocalIp = System.getenv("TESLA_HOST") == null ? NetUtils.getLocalHost() : System.getenv("TESLA_HOST");
        log.info("localIP:{},{}", localIp, tmpLocalIp);
        if (StringUtils.isNotEmpty(localIp)) {
            return localIp;
        }
        return tmpLocalIp;
    }

    public void addListener(Long spaceId, MilogConfigListener listener) {
        listeners.put(spaceId, listener);
    }

    public void listenMilogStreamConfig() {
        nacosConfig.addListener(spaceDataId, DEFAULT_GROUP_ID, new Listener() {
            @Override
            public Executor getExecutor() {
                return pool;
            }

            @Override
            public void receiveConfigInfo(String spaceStr) {

                try {
                    MiLogStreamConfig milogStreamConfig = new Gson().fromJson(spaceStr, MiLogStreamConfig.class);
                    String localIp = getLocalIp();
                    log.info("【监听namespace】收到了配置请求：{},localIp:{}", gson.toJson(milogStreamConfig), localIp);
                    if (null != milogStreamConfig) {
                        Map<String, Map<Long, String>> config = milogStreamConfig.getConfig();
                        if (null != config) {
                            if (config.containsKey(localIp)) {
                                Map<Long, String> dataIdMap = config.get(localIp);
                                // 删除旧的dataId监听，停止它下边dataId
                                stopUnusefulListenerAndJob(dataIdMap);
                                // 新增新的配置监听
                                startNewListenerAndJob(dataIdMap);
                            }
                        } else {
                            log.warn("listen dataID:{},groupId:{},but receive config is empty", spaceDataId, DEFAULT_GROUP_ID);
                        }
                    } else {
                        log.warn("listen dataID:{},groupId:{},but receive info is null", spaceDataId, DEFAULT_GROUP_ID);
                    }
                } catch (Exception e) {
                    log.error("【监听nameSpace】异常", e);
                }
            }
        });
    }

    /**
     * 新{spaceid,dataid} A 与内存中的{spaceid,dataid} B 进行比较，筛选出集合A-B
     *
     * @param spaceId
     * @return
     */
    public boolean existListener(Long spaceId) {
        if (milogSpaceDataMap.get(spaceId) == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 新{spaceid,dataid} A 与内存中的{spaceid,dataid} B 进行比较，筛选出集合B-A
     *
     * @param newMilogStreamDataMap 新的{spaceid,dataid}
     * @return 返回不再需要的{dataid}列表
     */
    public List<Long> unUseFilter(Map<Long, String> newMilogStreamDataMap) {
        List<Long> ret = new ArrayList<>();
        milogSpaceDataMap.forEach((spaceId, milogSpaceData) -> {
            // 存在于内存，不存在与最新Nacos配置
            if (!newMilogStreamDataMap.containsKey(spaceId)) {
                ret.add(spaceId);
            }
        });
        return ret;
    }

    /**
     * 1. 取消监听对应dataID
     * 2. 停止dataID配置下的job
     *
     * @param milogStreamDataMap
     */
    public void stopUnusefulListenerAndJob(Map<Long, String> milogStreamDataMap) {
        List<Long> unUseSpaceIds = unUseFilter(milogStreamDataMap);
        if (CollectionUtils.isEmpty(unUseSpaceIds)) {
            return;
        }
        log.info("【监听namespace】需要停止的spaceId:{}", gson.toJson(unUseSpaceIds));
        List<Long> listenerKeys = listeners.entrySet().stream().map(entry -> entry.getKey()).collect(Collectors.toList());
        log.info("【监听namespace】已经存在所有的监听器:{}", gson.toJson(listenerKeys));
        unUseSpaceIds.forEach(unUseSpaceId -> {
            MilogConfigListener milogSpaceConfigListener = listeners.get(unUseSpaceId);
            if (milogSpaceConfigListener != null) {
                log.info("被停止的spaceId:{}", unUseSpaceId);
                milogSpaceConfigListener.shutdown();
            } else {
                log.warn("当前监听器中不存在准备要停止的spaceId:{}", unUseSpaceId);
            }
            log.info("remove unUseSpaceId:{} log tail config listener", unUseSpaceId);
            listeners.remove(unUseSpaceId);

            // stop the job
            MilogSpaceData milogSpaceData = milogSpaceDataMap.get(unUseSpaceId);
            if (milogSpaceData != null) {
                milogSpaceConfigListener.getJobManager().closeJobs(milogSpaceData);
//                jobManager.closeJobs(milogSpaceData);
                log.info("close unUseSpaceId:{} logTail consumer job", unUseSpaceId);
            } else {
                log.warn("milog space config cache for spaceId:{},unuseful job,needed to be closed,but is null", unUseSpaceId);
            }
            milogSpaceDataMap.remove(unUseSpaceId);
        });
    }

    /**
     * 1. 监听对应dataId
     * 2. 启动job
     *
     * @param milogStreamDataMap
     */
    public void startNewListenerAndJob(Map<Long, String> milogStreamDataMap) {
        milogStreamDataMap.forEach((spaceId, dataId) -> {
            if (!existListener(spaceId)) { // 内存中不存在对应spaceid的linstener
                log.info("startNewListenerAndJob for spaceId:{}", spaceId);
                // 通过dataID 获取一份spaceData配置，放在configListener中缓存
                String milogSpaceDataStr = nacosConfig.getConfigStr(dataId, DEFAULT_GROUP_ID, DEFAULT_TIME_OUT_MS);
                MilogSpaceData milogSpaceData;
                if (StringUtils.isNotEmpty(milogSpaceDataStr)) {
                    milogSpaceData = new Gson().fromJson(milogSpaceDataStr, MilogSpaceData.class);
                } else {
                    milogSpaceData = new MilogSpaceData();
                }
                milogSpaceDataMap.put(spaceId, milogSpaceData);
                // Listen configuration
                MilogConfigListener configListener = new MilogConfigListener(spaceId, dataId, DEFAULT_GROUP_ID, milogSpaceData, nacosConfig);
                configListener.OnMessage();
                addListener(spaceId, configListener);
            }
        });
    }

    public ConcurrentHashMap<Long, MilogSpaceData> getMilogSpaceDataMap() {
        return milogSpaceDataMap;
    }

}
