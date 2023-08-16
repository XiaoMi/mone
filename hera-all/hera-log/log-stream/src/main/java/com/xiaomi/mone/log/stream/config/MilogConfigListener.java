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
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.model.LogtailConfig;
import com.xiaomi.mone.log.model.MilogSpaceData;
import com.xiaomi.mone.log.model.SinkConfig;
import com.xiaomi.mone.log.stream.job.JobManager;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.nacos.NacosConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.GSON;

@Component
@Slf4j
public class MilogConfigListener {
    private Long spaceId;
    private String dataId;
    private String group;
    private Listener listener;
    private MilogSpaceData milogSpaceData;

    private NacosConfig nacosConfig;

    @Getter
    private JobManager jobManager;

    private ReentrantLock lock = new ReentrantLock();

    private Gson gson = new Gson();
    /**
     * 不能用于判断 这个是持续递增的
     */
    private Map<Long, LogtailConfig> oldLogTailConfigMap = new HashMap<>();
    private Map<Long, SinkConfig> oldSinkConfigMap = new HashMap<>();

    public MilogConfigListener(Long spaceId, String dataId, String group, MilogSpaceData milogSpaceData, NacosConfig nacosConfig) {
        this.spaceId = spaceId;
        this.dataId = dataId;
        this.group = group;
        this.milogSpaceData = milogSpaceData;
        this.nacosConfig = nacosConfig;
        this.jobManager = new JobManager();
        this.listener = getListener(dataId, milogSpaceData);
        nacosConfig.addListener(dataId, group, listener);
    }

    private synchronized void handleNacosConfigDataJob(MilogSpaceData newMilogSpaceData) {
        if (!oldLogTailConfigMap.isEmpty() && !oldSinkConfigMap.isEmpty()) {
            List<SinkConfig> sinkConfigs = newMilogSpaceData.getSpaceConfig();
            compareOldStoreJobStop(sinkConfigs);
            for (SinkConfig sinkConfig : sinkConfigs) {
                compareOldTailIdJobStop(sinkConfig);
                if (oldSinkConfigMap.containsKey(sinkConfig.getLogstoreId())) {
                    //提交store信息是否变化，变化了都停止
                    if (!isStoreSame(sinkConfig, oldSinkConfigMap.get(sinkConfig.getLogstoreId()))) {
                        restartPerTail(sinkConfig, milogSpaceData);
                    } else {
                        comparePerTailHandle(sinkConfig, milogSpaceData);
                    }
                } else {
                    newStoreStart(sinkConfig, milogSpaceData);
                }
            }
        } else {
            // 重启所有的
            initNewJob(newMilogSpaceData);
        }
    }

    private void restartPerTail(SinkConfig sinkConfig, MilogSpaceData newMilogSpaceData) {
        //停止
        stopOldJobPerStore(sinkConfig.getLogstoreId());
        //重启
        for (LogtailConfig logtailConfig : sinkConfig.getLogtailConfigs()) {
            startTailPer(sinkConfig, logtailConfig, newMilogSpaceData.getMilogSpaceId());
        }
        oldSinkConfigMap.put(sinkConfig.getLogstoreId(), sinkConfig);
    }

    private void comparePerTailHandle(SinkConfig sinkConfig, MilogSpaceData newMilogSpaceData) {
        // 比较每个tail是否相同
        for (LogtailConfig logtailConfig : sinkConfig.getLogtailConfigs()) {
            if (!isTailSame(logtailConfig, oldLogTailConfigMap.get(logtailConfig.getLogtailId()))) {
                if (null != oldLogTailConfigMap.get(logtailConfig.getLogtailId())) {
                    stopOldJobPerTail(logtailConfig, sinkConfig);
                }
                startTailPer(sinkConfig, logtailConfig, newMilogSpaceData.getMilogSpaceId());
            }
        }
    }

    private void newStoreStart(SinkConfig sinkConfig, MilogSpaceData newMilogSpaceData) {
        // 新的store
        for (LogtailConfig logtailConfig : sinkConfig.getLogtailConfigs()) {
            startTailPer(sinkConfig, logtailConfig, newMilogSpaceData.getMilogSpaceId());
        }
        oldSinkConfigMap.put(sinkConfig.getLogstoreId(), sinkConfig);
    }

    private void stopAllJobClear() {
        //关闭所有的
        if (!oldSinkConfigMap.isEmpty()) {
            for (SinkConfig sinkConfig : oldSinkConfigMap.values()) {
                stopOldJobPerStore(sinkConfig.getLogstoreId());
            }
            oldSinkConfigMap.clear();
        }
    }

    private void compareOldTailIdJobStop(SinkConfig sinkConfig) {
        List<Long> newIds = sinkConfig.getLogtailConfigs().stream().map(LogtailConfig::getLogtailId).collect(Collectors.toList());
        List<Long> oldIds = Lists.newArrayList();
        if (oldSinkConfigMap.containsKey(sinkConfig.getLogstoreId())) {
            oldIds = oldSinkConfigMap.get(sinkConfig.getLogstoreId()).getLogtailConfigs().stream().map(LogtailConfig::getLogtailId).collect(Collectors.toList());
        }
        List<Long> collect = oldIds.stream().filter(tailId -> !newIds.contains(tailId)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            log.info("newIds:{},oldIds:{},collect:{}", gson.toJson(newIds), gson.toJson(oldIds), gson.toJson(collect));
            for (Long tailId : collect) {
                stopOldJobPerTail(oldLogTailConfigMap.get(tailId), sinkConfig);
            }
        }
    }

    private void compareOldStoreJobStop(List<SinkConfig> newSinkConfig) {
        List<Long> oldIds = oldSinkConfigMap.keySet().stream().collect(Collectors.toList());
        List<Long> newIds = newSinkConfig.stream().map(SinkConfig::getLogstoreId).collect(Collectors.toList());
        List<Long> collect = oldIds.stream().filter(tailId -> !newIds.contains(tailId)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            for (Long storeId : collect) {
                stopOldJobPerStore(storeId);
            }
        }
    }

    private boolean isStoreSame(SinkConfig newConfig, SinkConfig oldConfig) {
        if (null == oldConfig) {
            return false;
        }
        if (newConfig.equals(oldConfig)) {
            return true;
        }
        return false;
    }

    private boolean isTailSame(LogtailConfig newTail, LogtailConfig oldTail) {
        if (null == oldTail) {
            return false;
        }
        if (newTail.equals(oldTail)) {
            return true;
        }
        return false;
    }

    public MilogConfigListener() {
    }

    /**
     * stop old
     */
    private void stopOldJobPerStore(Long logStoreId) {
        SinkConfig sinkConfig = oldSinkConfigMap.get(logStoreId);
        if (null != sinkConfig) {
            log.info("【监听tail】要停止的任务：{}", gson.toJson(sinkConfig.getLogtailConfigs()));
            List<LogtailConfig> logTailConfigs = sinkConfig.getLogtailConfigs();
            for (LogtailConfig logTailConfig : logTailConfigs) {
                stopOldJobPerTail(logTailConfig, sinkConfig);
            }
        }
        oldSinkConfigMap.remove(logStoreId);
    }

    private void stopOldJobPerTail(LogtailConfig logTailConfig, SinkConfig sinkConfig) {
        log.info("【监听tail】需要停止旧的任务,oldTail{},oldEsIndex:{}", gson.toJson(logTailConfig), sinkConfig.getEsIndex());
        if (null != logTailConfig) {
            jobManager.stopJob(logTailConfig);
            oldLogTailConfigMap.remove(logTailConfig.getLogtailId());
        }
    }

    /**
     * start new
     *
     * @param newMilogSpaceData
     */
    private void initNewJob(MilogSpaceData newMilogSpaceData) {
        log.info("开始重新当前space的所有任务，spaceData:{}", gson.toJson(newMilogSpaceData));
        Map<Long, LogtailConfig> newLogTailConfigMap = new HashMap<>();
        Map<Long, SinkConfig> newSinkConfigMap = new HashMap<>();
        List<SinkConfig> newSpaceConfig = newMilogSpaceData.getSpaceConfig();
        if (newSpaceConfig != null) {
            for (SinkConfig sinkConfig : newSpaceConfig) {
                List<LogtailConfig> logTailConfigs = sinkConfig.getLogtailConfigs();
                if (logTailConfigs != null) {
                    for (LogtailConfig logTailConfig : logTailConfigs) {
                        if (null != logTailConfig) {
                            newLogTailConfigMap.put(logTailConfig.getLogtailId(), logTailConfig);
                            startTailPer(sinkConfig, logTailConfig, newMilogSpaceData.getMilogSpaceId());
                        }
                    }
                }
                newSinkConfigMap.put(sinkConfig.getLogstoreId(), sinkConfig);
            }
        }
        milogSpaceData = newMilogSpaceData;
        oldLogTailConfigMap = newLogTailConfigMap;
        oldSinkConfigMap = newSinkConfigMap;
    }

    private void startTailPer(SinkConfig sinkConfig, LogtailConfig logTailConfig, Long logSpaceId) {
        log.info("【监听tail】初始化新任务，tail配置：{},索引:{}.集群信息：{}", gson.toJson(logTailConfig), sinkConfig.getEsIndex(), gson.toJson(sinkConfig.getEsInfo()));
        jobManager.startJob(logTailConfig, sinkConfig.getEsIndex(), sinkConfig.getKeyList(), sinkConfig.getLogstoreName(), logTailConfig.getTail(), sinkConfig.getEsInfo(),
                sinkConfig.getLogstoreId(), logSpaceId);
        oldLogTailConfigMap.put(logTailConfig.getLogtailId(), logTailConfig);
    }

    private static ExecutorService THREAD_POOL = ExecutorBuilder.create()
            .setCorePoolSize(5)//初始池大小
            .setMaxPoolSize(30) //最大池大小
            .setWorkQueue(new LinkedBlockingQueue<>(1000))//最大等待数为100
            .setThreadFactory(ThreadFactoryBuilder.create().setNamePrefix("Listen-tail-Pool-").build())// 线程池命名
            .build();

    @NotNull
    private Listener getListener(String dataId, MilogSpaceData milogSpaceData) {
        return new Listener() {
            @Override
            public Executor getExecutor() {
                return THREAD_POOL;
            }

            @Override
            public void receiveConfigInfo(String dataValue) {
                try {
                    log.info("【监听tail】收到了配置请求：{},已经存在的配置:storeMap:{},tailMap:{}", dataValue, gson.toJson(oldSinkConfigMap), gson.toJson(oldLogTailConfigMap));
                    if (StringUtils.isNotEmpty(dataValue) && !Constant.NULLVALUE.equals(dataValue)) {
                        MilogSpaceData newMilogSpaceData = GSON.fromJson(dataValue, MilogSpaceData.class);
                        if (null == newMilogSpaceData || CollectionUtils.isEmpty(newMilogSpaceData.getSpaceConfig())) {
                            log.error("【监听tail】收到的配置错误，dataId:{},spaceId:{}", dataId, milogSpaceData.getMilogSpaceId());
                            return;
                        }
                        handleNacosConfigDataJob(newMilogSpaceData);
                    } else {
                        stopAllJobClear();
                    }
                } catch (Exception e) {
                    log.error(String.format("【监听tail】异常,dataId:%s", dataId), e);
                }
            }
        };
    }

    public void shutdown() {
        // 取消监听spaceId对应的配置监听器
        if (this.listener != null) {
            nacosConfig.removeListener(this.dataId, this.group, this.listener);
        }
    }
}
