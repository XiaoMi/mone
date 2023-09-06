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
package com.xiaomi.mone.log.stream.config;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.alibaba.nacos.api.config.listener.Listener;
import com.google.gson.Gson;
import com.xiaomi.mone.log.model.MiLogStreamConfig;
import com.xiaomi.mone.log.model.MilogSpaceData;
import com.xiaomi.mone.log.stream.common.util.StreamUtils;
import com.xiaomi.mone.log.stream.exception.StreamException;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.docean.plugin.nacos.NacosConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.*;

@Service
@Slf4j
public class ConfigManager {
    @Resource
    private NacosConfig nacosConfig;


    @Value("${hera.stream.monitor_space_data_id}")
    private String spaceDataId;

    //final String spaceDataId = LOG_MANAGE_PREFIX + NAMESPACE_CONFIG_DATA_ID;

    /**
     * Stores the Milog Space Config Listener managed by Config Manager
     * key: spaceId
     * value: MilogSpaceConfigListener
     */
    @Getter
    private ConcurrentHashMap<Long, MilogConfigListener> listeners = new ConcurrentHashMap<>();

    /**
     * The Milog Space Data that this instance needs to listen on
     * key: spaceId
     * value: milogSpaceData
     */
    private ConcurrentHashMap<Long, MilogSpaceData> milogSpaceDataMap = new ConcurrentHashMap<>();

    private Gson gson = new Gson();

    /**
     * Executed once when the service starts
     *
     * @throws StreamException
     */
    public void initStream() throws StreamException {
        log.debug("[initStream} nacos dataId:{},group:{}", spaceDataId, DEFAULT_GROUP_ID);
        String streamConfigStr = nacosConfig.getConfigStr(spaceDataId, DEFAULT_GROUP_ID, DEFAULT_TIME_OUT_MS);
        MiLogStreamConfig milogStreamConfig;
        try {
            if (StringUtils.isNotEmpty(streamConfigStr) && !NULLVALUE.equals(streamConfigStr)) {
                milogStreamConfig = GSON.fromJson(streamConfigStr, MiLogStreamConfig.class);
            } else {
                log.warn("[ConfigManager.initConfigManager] Nacos configuration [dataID:{},group:{}]not found,exit initConfigManager", spaceDataId, DEFAULT_GROUP_ID);
                return;
            }
            String uniqueMark = StreamUtils.getCurrentMachineMark();
            Map<String, Map<Long, String>> config = milogStreamConfig.getConfig();
            if (config.containsKey(uniqueMark)) {
                Map<Long, String> milogStreamDataMap = config.get(uniqueMark);
                log.info("[ConfigManager.initConfigManager] uniqueMark:{},data:{}", uniqueMark, gson.toJson(milogStreamDataMap));
                for (Long spaceId : milogStreamDataMap.keySet()) {
                    final String dataId = milogStreamDataMap.get(spaceId);
                    // init spaceData config
                    String milogSpaceDataStr = nacosConfig.getConfigStr(dataId, DEFAULT_GROUP_ID, DEFAULT_TIME_OUT_MS);
                    if (StringUtils.isNotEmpty(milogSpaceDataStr)) {
                        MilogSpaceData milogSpaceData = GSON.fromJson(milogSpaceDataStr, MilogSpaceData.class);
                        if (null != milogSpaceData) {
                            milogSpaceDataMap.put(spaceId, milogSpaceData);
                        }
                    }
                    MilogSpaceData milogSpaceData = milogSpaceDataMap.get(spaceId);
                    if (null != milogSpaceData) {
                        MilogConfigListener configListener = new MilogConfigListener(spaceId, dataId, DEFAULT_GROUP_ID, milogSpaceData, nacosConfig);
                        addListener(spaceId, configListener);
                    }
                    log.info("[ConfigManager.initStream] added log config listener for spaceId:{},dataId:{}", spaceId, dataId);
                }
            } else {
                log.info("server start current not contain space config,uniqueMark:{}", uniqueMark);
            }
        } catch (Exception e) {
            log.error("[ConfigManager.initStream] initStream exec err", e);
        }
    }


    public void addListener(Long spaceId, MilogConfigListener listener) {
        listeners.put(spaceId, listener);
    }

    private static ExecutorService THREAD_POOL = ExecutorBuilder.create()
            .setCorePoolSize(5)
            .setMaxPoolSize(30)
            .setWorkQueue(new LinkedBlockingQueue<>(1000))
            .setThreadFactory(ThreadFactoryBuilder.create().setNamePrefix("Listen-space-Pool-").build())
            .build();

    public void listenMilogStreamConfig() {
        nacosConfig.addListener(spaceDataId, DEFAULT_GROUP_ID, new Listener() {
            @Override
            public Executor getExecutor() {
                return THREAD_POOL;
            }

            @Override
            public void receiveConfigInfo(String spaceStr) {

                try {
                    MiLogStreamConfig milogStreamConfig = GSON.fromJson(spaceStr, MiLogStreamConfig.class);
                    String uniqueMark = StreamUtils.getCurrentMachineMark();
                    log.info("Listening namespace received a configuration request,{},uniqueMark:{}", gson.toJson(milogStreamConfig), uniqueMark);
                    if (null != milogStreamConfig) {
                        Map<String, Map<Long, String>> config = milogStreamConfig.getConfig();
                        if (null != config) {
                            if (config.containsKey(uniqueMark)) {
                                Map<Long, String> dataIdMap = config.get(uniqueMark);
                                // Delete the old data ID listener and stop the data ID underneath it
                                stopUnusefulListenerAndJob(dataIdMap);
                                // Added a new configuration listener
                                startNewListenerAndJob(dataIdMap);
                            }
                        } else {
                            log.warn("listen dataID:{},groupId:{},but receive config is empty", spaceDataId, DEFAULT_GROUP_ID);
                        }
                    } else {
                        log.warn("listen dataID:{},groupId:{},but receive info is null", spaceDataId, DEFAULT_GROUP_ID);
                    }
                } catch (Exception e) {
                    log.error("Listen to name Space exception", e);
                }
            }
        });
    }

    /**
     * The new {spaceid,dataid} A is compared to {spaceid,dataid} B in memory to filter out the sets A-B
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
     * The new {spaceid,dataid} A is compared to {spaceid,dataid} B in memory and filtered out the set B-A
     *
     * @param newMilogStreamDataMap new {spaceid,dataid}
     * @return Returns a list of {dataid} that are no longer needed
     */
    public List<Long> unUseFilter(Map<Long, String> newMilogStreamDataMap) {
        List<Long> ret = new ArrayList<>();
        milogSpaceDataMap.forEach((spaceId, milogSpaceData) -> {
            // Exists in memory, does not exist with the latest Nacos configuration
            if (!newMilogStreamDataMap.containsKey(spaceId)) {
                ret.add(spaceId);
            }
        });
        return ret;
    }

    /**
     * 1. Cancel the listener corresponding to the data ID
     * 2. Stop the job in the data ID configuration
     *
     * @param milogStreamDataMap
     */
    public void stopUnusefulListenerAndJob(Map<Long, String> milogStreamDataMap) {
        List<Long> unUseSpaceIds = unUseFilter(milogStreamDataMap);
        if (CollectionUtils.isEmpty(unUseSpaceIds)) {
            return;
        }
        log.info("【Listening namespace】The space ID that needs to be stopped:{}", gson.toJson(unUseSpaceIds));
        List<Long> listenerKeys = listeners.entrySet().stream().map(entry -> entry.getKey()).collect(Collectors.toList());
        log.info("[Listening namespace] All listeners already exist:{}", gson.toJson(listenerKeys));
        unUseSpaceIds.forEach(unUseSpaceId -> {
            MilogConfigListener milogSpaceConfigListener = listeners.get(unUseSpaceId);
            if (milogSpaceConfigListener != null) {
                log.info("The stopped space ID:{}", unUseSpaceId);
                milogSpaceConfigListener.shutdown();
            } else {
                log.warn("There is no space ID in the current listener that is ready to be stopped:{}", unUseSpaceId);
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
     * 1. The listener corresponds to the data ID
     * 2. start job
     *
     * @param milogStreamDataMap
     */
    public void startNewListenerAndJob(Map<Long, String> milogStreamDataMap) {
        milogStreamDataMap.forEach((spaceId, dataId) -> {
            if (!existListener(spaceId)) { // There is no linstener corresponding to the spaceid in memory
                log.info("startNewListenerAndJob for spaceId:{}", spaceId);
                // Get a copy of the spaceData configuration through the dataID and put it in the configListener cache
                String milogSpaceDataStr = nacosConfig.getConfigStr(dataId, DEFAULT_GROUP_ID, DEFAULT_TIME_OUT_MS);
                MilogSpaceData milogSpaceData;
                if (StringUtils.isNotEmpty(milogSpaceDataStr)) {
                    milogSpaceData = GSON.fromJson(milogSpaceDataStr, MilogSpaceData.class);
                } else {
                    milogSpaceData = new MilogSpaceData();
                }
                milogSpaceDataMap.put(spaceId, milogSpaceData);
                // Listen configuration
                MilogConfigListener configListener = new MilogConfigListener(spaceId, dataId, DEFAULT_GROUP_ID, milogSpaceData, nacosConfig);
                addListener(spaceId, configListener);
            }
        });
    }

    public ConcurrentHashMap<Long, MilogSpaceData> getMilogSpaceDataMap() {
        return milogSpaceDataMap;
    }

}
