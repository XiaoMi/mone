package com.xiaomi.mone.log.stream.job;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.log.model.EsInfo;
import com.xiaomi.mone.log.model.LogtailConfig;
import com.xiaomi.mone.log.model.MilogSpaceData;
import com.xiaomi.mone.log.model.SinkConfig;
import com.xiaomi.mone.log.stream.common.LogStreamConstants;
import com.xiaomi.mone.log.stream.common.SinkJobEnum;
import com.xiaomi.mone.log.stream.job.extension.SinkJob;
import com.xiaomi.mone.log.stream.job.extension.SinkJobProvider;
import com.xiaomi.mone.log.stream.sink.SinkChain;
import com.xiaomi.youpin.docean.Ioc;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Data
@Slf4j
public class JobManager {
    /**
     * key: logstoreId
     */
    private ConcurrentHashMap<Long, List<SinkJob>> jobs = new ConcurrentHashMap<>();

    private SinkChain sinkChain = Ioc.ins().getBean(SinkChain.class);

    private Gson gson = new Gson();

    public void closeJobs(MilogSpaceData milogSpaceData) {
        List<SinkConfig> spaceConfig = milogSpaceData.getSpaceConfig();
        log.info("已经在运行的任务：{}", gson.toJson(jobs));
        log.info("正要被关掉的任务：{}", gson.toJson(milogSpaceData));
        if (spaceConfig != null) {
            spaceConfig.forEach(sinkConfig -> {
                List<LogtailConfig> logtailConfigs = sinkConfig.getLogtailConfigs();
                if (logtailConfigs != null) {
                    logtailConfigs.forEach(logtailConfig -> {
                        try {
                            sinkJobsShutDown(logtailConfig);
                        } catch (Exception e) {
                            log.error(String.format("[JobManager.closeJobs] closeJob err,logtailId:%s", logtailConfig.getLogtailId()), e);
                        }
                    });
                }
            });
        }
    }

    private void sinkJobsShutDown(LogtailConfig logtailConfig) {
        List<SinkJob> sinkJobs = jobs.get(logtailConfig.getLogtailId());
        if (CollectionUtils.isNotEmpty(sinkJobs)) {
            sinkJobs.forEach(sinkJob -> {
                try {
                    sinkJob.shutdown();
                } catch (Exception e) {
                    log.error("[JobManager.shutdown] closeJobs.shutdown error,logTailID:{}", logtailConfig.getLogtailId(), e);
                }
            });
        } else {
            log.warn("[JobManager.closeJobs] closeJobs.shutdown error,old job is null,logtailID:{}", logtailConfig.getLogtailId());
        }
        jobs.remove(logtailConfig.getLogtailId());
    }

    public synchronized void stopJob(LogtailConfig logtailConfig) {
        try {
            List<Long> jobKeys = jobs.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
            log.info("【stop job】,all jobs:{}", jobKeys);
            sinkJobsShutDown(logtailConfig);
        } catch (Exception e) {
            log.error(String.format("[JobManager.stopJob] stopJob err,logtailId:%s", logtailConfig.getLogtailId()), e);
        }
    }

    private void startConsumerJob(String type, String ak, String sk, String clusterInfo, LogtailConfig
            logtailConfig, String keyList, String logStoreName, String esIndex, EsInfo esInfo, Long logStoreId, Long logSpaceId) {
        try {
            SinkJobConfig sinkJobConfig = SinkJobConfig.builder()
                    .mqType(type)
                    .ak(ak)
                    .sk(sk)
                    .clusterInfo(clusterInfo)
                    .topic(logtailConfig.getTopic())
                    .tag(logtailConfig.getTag())
                    .index(esIndex)
                    .keyList(keyList)
                    .valueList(logtailConfig.getValueList())
                    .parseScript(logtailConfig.getParseScript())
                    .logStoreName(logStoreName)
                    .sinkChain(this.getSinkChain())
                    .tail(logtailConfig.getTail())
                    .esInfo(esInfo)
                    .parseType(logtailConfig.getParseType())
                    .jobType(SinkJobEnum.NORMAL_JOB.name())
                    .build();
            sinkJobConfig.setLogTailId(logtailConfig.getLogtailId());
            sinkJobConfig.setLogStoreId(logStoreId);
            sinkJobConfig.setLogSpaceId(logSpaceId);
            log.warn("##startConsumerJob## spaceId:{}, storeId:{}, tailId:{}", sinkJobConfig.getLogSpaceId(), sinkJobConfig.getLogStoreId(), sinkJobConfig.getLogTailId());

            String sinkProviderBean = sinkJobConfig.getMqType() + LogStreamConstants.sinkJobProviderBeanSuffix;
            SinkJobProvider sinkJobProvider = Ioc.ins().getBean(sinkProviderBean);
            SinkJob instanceSinkJobEs = sinkJobProvider.getSinkJob(sinkJobConfig);

            if (instanceSinkJobEs.start()) {
                jobs.putIfAbsent(logtailConfig.getLogtailId(), Lists.newArrayList());
                jobs.get(logtailConfig.getLogtailId()).add(instanceSinkJobEs);
            }
            SinkJob providerBackupJob = sinkJobProvider.getBackupJob(sinkJobConfig);
            if (null != providerBackupJob && providerBackupJob.start()) {
                jobs.get(logtailConfig.getLogtailId()).add(providerBackupJob);
            }
            log.info(String.format("[JobManager.initJobs] startJob success,logTailId:%s,topic:%s,tag:%s,esIndex:%s", logtailConfig.getLogtailId(), logtailConfig.getTopic(), logtailConfig.getTag(), esIndex));
        } catch (Exception e) {
            log.error(String.format("[JobManager.initJobs] startJob err,logTailId:%s,topic:%s,tag:%s,esIndex:%s", logtailConfig.getLogtailId(), logtailConfig.getTopic(), logtailConfig.getTag(), esIndex), e);
        }
    }

    public synchronized void startJob(LogtailConfig logtailConfig, String esIndex, String keyList, String logStoreName,
                                      String tail, EsInfo esInfo, Long logStoreId, Long logSpaceId) {
        try {
            String ak = logtailConfig.getAk();
            String sk = logtailConfig.getSk();
            String clusterInfo = logtailConfig.getClusterInfo();
            String type = logtailConfig.getType();
            if (StringUtils.isEmpty(clusterInfo) || StringUtils.isEmpty(logtailConfig.getTopic())) {
                log.info("start job error,ak or sk or logtailConfig null,ak:{},sk:{},logtailConfig:{}", ak, sk, new Gson().toJson(logtailConfig));
                return;
            }
            startConsumerJob(type, ak, sk, clusterInfo, logtailConfig, keyList, logStoreName, esIndex, esInfo, logStoreId, logSpaceId);
        } catch (Exception e) {
            log.error(String.format("[JobManager.startJob] start job err,logtailConfig:%s,esIndex:%s", logtailConfig, esIndex), e);
        }
    }

    public void stopAllJob() {
        for (Map.Entry<Long, List<SinkJob>> sinkJobEntry : jobs.entrySet()) {
            sinkJobEntry.getValue().forEach(sinkJob -> {
                try {
                    sinkJob.shutdown();
                } catch (Exception e) {
                    log.error("[JobManager.shutdown] closeJobs.shutdown error,logTailID:{}", sinkJobEntry.getKey(), e);
                }
            });
        }
        jobs.clear();
    }
}
