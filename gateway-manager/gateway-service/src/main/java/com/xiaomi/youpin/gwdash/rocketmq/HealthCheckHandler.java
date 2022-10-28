///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.rocketmq;
//
//import com.dianping.cat.Cat;
//import com.google.gson.Gson;
//import com.xiaomi.youpin.gwdash.bo.DeployMachine;
//import com.xiaomi.youpin.gwdash.bo.ScaleType;
//import com.xiaomi.youpin.gwdash.common.AutoScalingUtils;
//import com.xiaomi.youpin.gwdash.common.LabelUtils;
//import com.xiaomi.youpin.gwdash.dao.model.ErrorContent;
//import com.xiaomi.youpin.gwdash.dao.model.MError;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectEnvDeploySetting;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
//import com.xiaomi.youpin.gwdash.service.PipelineService;
//import com.xiaomi.youpin.gwdash.service.ProjectDeploymentService;
//import com.xiaomi.youpin.mischedule.api.service.bo.HealthResult;
//import com.xiaomi.youpin.mischedule.api.service.bo.ServiceInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.nutz.dao.Chain;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * @author renqingfu
// * @author goodjava@qq.com
// * @author zheng.xucn@outlook.com
// */
//@Component
//@Slf4j
//public class HealthCheckHandler {
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//
//    @Value("${rocket.tag.healthcheck}")
//    private String healthCheckTag;
//
//    @Autowired
//    private AutoScalingUtils autoScalingUtils;
//
//    @Autowired
//    private ProjectDeploymentService deploymentService;
//
//
//    public static String tag;
//
//    private static final int CAPACITY = 5000;
//
//    private static final long QPS_INVALID = -1l;
//    private static final String SCALE_FACTOR_LABEL_KEY = "scale_factor";
//    private static final String DISABLE_SCALE_DOWN_LABEL_KEY = "disable_scale_down";
//
//
//    private ArrayBlockingQueue<HealthResult> queue = new ArrayBlockingQueue<>(CAPACITY);
//
//
//    private ReentrantLock lock = new ReentrantLock();
//
//    private static ThreadPoolExecutor cachedThreadPool =
//            (ThreadPoolExecutor) Executors.newCachedThreadPool();
//
//    @PostConstruct
//    public void init() {
//        tag = healthCheckTag;
//        Executors.newSingleThreadExecutor().submit(() -> {
//            while (true) {
//                try {
//                    HealthResult hr = this.queue.take();
//                    consumeMessage(hr);
//                } catch (Exception e) {
//                    log.error(e.toString());
//                    Cat.logError(e);
//                }
//            }
//        });
//    }
//
//    private HashSet<String> deployedMachines(List<DeployMachine> dockerMachineList) {
//        HashSet<String> ips = new HashSet<>();
//        for (DeployMachine machine : dockerMachineList) {
//            ips.add(machine.getIp());
//        }
//        return ips;
//    }
//
//    private boolean isMachineDeployed(HashSet<String> deployedMachines, String ip) {
//        if (StringUtils.isEmpty(ip)) {
//            return false;
//        }
//        return deployedMachines.contains(ip);
//    }
//
//
//    private void consumeMessage(HealthResult hr) {
//
//        long now = System.currentTimeMillis();
//        if (hr.getServiceInfoList().size() == 0) {
//            return;
//        }
//        long envId = hr.getPipelineInfo().getEnvId();
//        ProjectEnvDeploySetting ds = dao.fetch(ProjectEnvDeploySetting.class, Cnd.where("env_id", "=", envId));
//        if (!Optional.ofNullable(ds).isPresent()) {
//            log.warn("ProjectEnvDeploySetting is null:{}", envId);
//            return;
//        }
//
//        ProjectPipeline pipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
//        if (null == pipeline) {
//            log.warn("pipeline is null env id: {}", envId);
//            return;
//        }
//
//        List<DeployMachine> dockerMachineList = pipeline.getDeployInfo().getDockerMachineList();
//        HashSet<String> deployedMachines = deployedMachines(dockerMachineList);
//
//        //当前的副本数量
//        int curReplicate = dockerMachineList.size();
//
//        String labels = ds.getLabels();
//        //在label中指定qps
//        String value = LabelUtils.getLabelValue(labels, "qps");
//        String scaleFactorLabel = LabelUtils.getLabelValue(labels, SCALE_FACTOR_LABEL_KEY);
//        String disableScaleDownLabel = LabelUtils.getLabelValue(labels, DISABLE_SCALE_DOWN_LABEL_KEY);
//
//        //没有qps的情况下,只支持健康监测
//        if (!StringUtils.isEmpty(value)) {
//            try {
//                expansionOrShrink(now, hr, envId, ds, dockerMachineList, deployedMachines, value, scaleFactorLabel, disableScaleDownLabel);
//            } catch (Exception ex) {
//                log.error("expansionOrShrink " + envId + " error:" + ex.toString(), ex);
//                Cat.logError(ex);
//            }
//        }
//
//        hr.getServiceInfoList().stream().forEach(info -> {
//            MError error = new MError();
//            error.setCtime(now);
//            error.setUtime(now);
//            error.setIp(info.getIp());
//            ErrorContent content = new ErrorContent();
//            content.setEnvId(hr.getPipelineInfo().getEnvId());
//            content.setPipelineId(hr.getPipelineInfo().getPipelineId());
//            content.setProjectId(hr.getPipelineInfo().getProjectId());
//            content.setQps(info.getQps());
//            content.setReplicate(curReplicate);
//            error.setContent(content);
//
//            //挂掉的服务器
//            if (info.getStatus() != 0 && isMachineDeployed(deployedMachines, info.getIp())) {
//                //保存重启事件
//                restartEvent(now, envId, info, error);
//                Cat.logEvent("restartEvent", "envId: " + hr.getPipelineInfo().getEnvId(), "0", "ip: " + info.getIp());
//            }
//        });
//        log.info("handleHealthCheck :{}", hr);
//        //更新env 尽量避免修改其他数据(有竞态关系)
//        dao.update("project_env", Chain.make("health_check_result", new Gson().toJson(hr)), Cnd.where("id", "=", envId));
//
//
//    }
//
//
//    /**
//     * 存储健康监测结果
//     *
//     * @param message
//     */
//    public void consumeMessage(MessageExt message) {
//        log.info("HealthCheckHandler#consumeMessage: {} {}", message.getMsgId(), new String(message.getBody()));
//        try {
//            byte[] body = message.getBody();
//            //返回的是一组qps信息
//            HealthResult hr = new Gson().fromJson(new String(body), HealthResult.class);
//
//            boolean success = queue.offer(hr);
//            if (!success) {
//                log.warn("health check queue size:{}", this.queue.size());
//            }
//        } catch (Throwable ex) {
//            log.warn("health check error:" + ex.getMessage(), ex);
//            Cat.logError(ex);
//        }
//    }
//
//
//    /**
//     * 计算每台机器qps之和.
//     *
//     * @param list 机器qps list
//     * @return 返回qps之和
//     */
//    private long getTotalQPS(List<Long> list) {
//        long total = 0;
//        for (long qps : list) {
//            total += qps;
//        }
//        return total;
//    }
//
//    /**
//     * 判断能否缩容
//     * 当前机器数量必须大于1
//     * 减少一台机器以后,每台机器的平均qps要小于qpsLimit参数
//     * 如果大于或等于qpsLimit就无需缩容.
//     * 目的:避免缩容以后马上又扩容
//     *
//     * @param curReplicate 当前机器数量
//     * @param qpsLimit
//     * @param totalQps
//     * @return
//     */
//    private boolean canScaleDown(int curReplicate, long qpsLimit, double totalQps) {
//        return curReplicate - 1 > 0 && totalQps / (curReplicate - 1) < qpsLimit;
//    }
//
//
//    private HashMap<String, Long> ipQpsMapping(HealthResult healthResult) {
//        HashMap<String, Long> map = new HashMap<>();
//        for (ServiceInfo info : healthResult.getServiceInfoList()) {
//            if (info.getQps() != QPS_INVALID) {
//                map.put(info.getIp(), info.getQps());
//            }
//        }
//        return map;
//    }
//
//    private boolean isReplicateActive(Long replicateQps) {
//        return replicateQps != null && replicateQps != QPS_INVALID;
//    }
//
//    private void expansionOrShrink(long now, HealthResult hr, long envId, ProjectEnvDeploySetting ds, List<DeployMachine> dockerMachineList, HashSet<String> deployedMachines, String value, String scaleFactorLabel,
//                                   String disableScaleDownLabel) {
//        long qpsLimit = StringUtils.isNotEmpty(value) ? Long.valueOf(value) : 0L;
//        if (qpsLimit <= 0L || dockerMachineList == null) {
//            return;
//        }
//
//        log.info("scale factor label envId:{} value:{}", envId, scaleFactorLabel);
//        int scaleFactor = StringUtils.isEmpty(scaleFactorLabel) ? -1 : Integer.parseInt(scaleFactorLabel);
//        //true=不能缩容
//        boolean disableScaleDown = StringUtils.isNotEmpty(disableScaleDownLabel) && disableScaleDownLabel.equals("true");
//
//        int curReplicate = dockerMachineList.size();
//        if (curReplicate == 0) {
//            return;
//        }
//
//        long maxReplicate = ds.getMaxReplicate();
//        long minReplicate = ds.getReplicate();
//
//        //拥有qps的机器的数量
//        int activeReplicates = 0;
//        HashMap<String, Long> ipQpsMap = ipQpsMapping(hr);
//
//        //返回qps超过阈值的机器的数量
//        int overQpsLimitCount = 0;
//        //qps低于阈值的机器的数量
//        int underQpsLimitCount = 0;
//        double totalQps = 0;
//        for (DeployMachine machine : dockerMachineList) {
//            Long replicateQps = ipQpsMap.get(machine.getIp());
//            if (isReplicateActive(replicateQps)) {
//                activeReplicates++;
//                if (replicateQps >= qpsLimit) {
//                    overQpsLimitCount++;
//                } else {
//                    underQpsLimitCount++;
//                }
//                totalQps += replicateQps;
//            }
//        }
//
//
//        log.info("expansion envId:{} curReplicate:{} activeReplicates:{} maxReplicate:{} minReplicate:{} expansionSize:{}", envId, curReplicate, activeReplicates, maxReplicate, minReplicate, overQpsLimitCount);
//
//
//        //需要扩容 扩容优先级高
//        if ((curReplicate < maxReplicate) &&
//                ((activeReplicates == 0 && curReplicate > activeReplicates) || (overQpsLimitCount > 0 && overQpsLimitCount >= activeReplicates / 2))) {
//
//            long recommendedReplicateCount = recommendReplicateCount(curReplicate, maxReplicate, totalQps, qpsLimit, activeReplicates, scaleFactor);
//            log.info("recommendedReplicateCount envId:{} count:{}", envId, recommendedReplicateCount);
//            cachedThreadPool.submit(() -> {
//                deploymentService.autoScale(String.valueOf(envId), ScaleType.expansion.name(), recommendedReplicateCount);
//            });
//
//            return;
//        }
//
//
//        if (disableScaleDown) {
//            //通过设置labels,这个环境的用户不让缩容
//            return;
//        }
//
//        //扩容后1小时内不允许自动缩容
//        if (!autoScalingUtils.allowScaleDown(envId)) {
//            return;
//        }
//
//        if (!canScaleDown(activeReplicates, qpsLimit, totalQps)) {
//            return;
//        }
//
//        //扩容和缩容不能同时进行
//
//        log.info("Shrink envId:{} curReplicate:{} activeReplicates:{} minReplicate:{} shrinkCount:{}", envId, curReplicate, activeReplicates, minReplicate, underQpsLimitCount);
//        //需要缩容
//        if (activeReplicates > Math.max(1, minReplicate) && underQpsLimitCount > activeReplicates / 2) {
//
//            cachedThreadPool.submit(() -> {
//                deploymentService.autoScale(String.valueOf(envId), ScaleType.shrink.name(), -1);
//            });
//        }
//    }
//
//    private long recommendReplicateCount(long curReplicate, long maxReplicate, double totalQps, long qpsLimit, int activeReplicates, int scaleFactor) {
//        if (activeReplicates == 0 && curReplicate > activeReplicates) {
//            return curReplicate + 1;
//        }
//
//        long replicatesNeeded = Math.max(curReplicate + 1, (long) Math.ceil(totalQps / qpsLimit));
//        if (isScaleFactorValid(scaleFactor)) {
//            double percentage = 0.01;
//            replicatesNeeded = Math.max(curReplicate + 1, (long) Math.ceil(curReplicate + scaleFactor * percentage * maxReplicate));
//        }
//        if (activeReplicates < curReplicate) {
//            replicatesNeeded += (curReplicate - activeReplicates);
//        }
//        return Math.min(replicatesNeeded, maxReplicate);
//    }
//
//    private boolean isScaleFactorValid(int scaleFactor) {
//        return scaleFactor >= 10 && scaleFactor <= 100;
//    }
//
//    private void restartEvent(long now, long envId, ServiceInfo info, MError error) {
//        String ip = info.getIp();
//        //避免错误日志膨胀,也可以做时间维度的处理
//        String key = Stream.of(ip, String.valueOf(envId)).collect(Collectors.joining("_"));
//        MError merror = dao.fetch(MError.class, Cnd.where("key", "=", key)
//                .and("type", "=", MError.ErrorType.HealthCheck.ordinal()));
//        if (null == merror) {
//            error.setType(MError.ErrorType.HealthCheck.ordinal());
//            error.setKey(key);
//            dao.insert(error);
//        } else {
//            //每次有一分钟的处理时间
//            if (merror.getUtime() + TimeUnit.MINUTES.toMillis(1) < now) {
//                merror.setUtime(now);
//                merror.setStatus(0);
//                merror.setServiceName(envId + ":重启");
//                dao.update(merror);
//            }
//        }
//    }
//
//
//    private void saveHealthEvent(MError event, long envId, long now, long realQps, List<Long> qpsList, long qps, ScaleType type, long recommendedReplicateCount) {
//        boolean autoScale = false;
//
//        try {
//            boolean l = lock.tryLock(2, TimeUnit.SECONDS);
//            if (l) {
//                try {
//                    //qps负载过高的服务器,维度是envId
//                    int eventType = type.equals(ScaleType.expansion) ? MError.ErrorType.HealthCheckLoadHigh.ordinal() : MError.ErrorType.HealthCheckLoadLow.ordinal();
//                    String message = type.equals(ScaleType.expansion) ? ":自动扩容:" : ":自动缩容:";
//                    MError merror = dao.fetch(MError.class, Cnd.where("key", "=", envId).and("type", "=", eventType));
//                    if (null == merror) {
//                        autoScale = true;
//                        //把statue设成1,这样不使用MErrorService来做auto scaling
//                        event.setStatus(1);
//                        event.setCtime(now);
//                        event.setUtime(now);
//                        event.setServiceName(envId + message + realQps + ":" + qps);
//                        event.setKey(String.valueOf(envId));
//                        event.setType(eventType);
//                        event.setContent(new ErrorContent());
//                        event.getContent().setEnvId(envId);
//                        event.getContent().setQpsList(qpsList);
//                        event.getContent().setRecommendedReplicateCount(recommendedReplicateCount);
//                        dao.insert(event);
//                    } else {
//                        if (merror.getUtime() + TimeUnit.SECONDS.toMillis(31) < now) {
//                            autoScale = true;
//                            merror.setUtime(now);
//
//                            // merror.setStatus(0);
//                            //把statue设成1,这样不使用MErrorService来做auto scaling
//                            merror.setStatus(1);
//
//                            if (null == merror.getContent()) {
//                                merror.setContent(new ErrorContent());
//                            }
//                            merror.getContent().setEnvId(envId);
//                            merror.getContent().setQps(realQps);
//                            merror.getContent().setQpsList(qpsList);
//                            merror.getContent().setRecommendedReplicateCount(recommendedReplicateCount);
//                            merror.setServiceName(envId + message + realQps + ":" + qps);
//                            dao.update(merror);
//                        }
//                    }
//                } catch (Exception e) {
//                    log.warn("saveHealthEvent failed");
//                    log.error(e.toString());
//                    //cat logging
//                    Cat.logError(e);
//                    throw e;
//                } finally {
//                    lock.unlock();
//                }
//            }
//        } catch (InterruptedException e) {
//            log.warn("saveHealthEvent error:{}", e.getMessage());
//            Cat.logError(e);
//        }
//
//        if (autoScale) {
//            deploymentService.autoScale(String.valueOf(envId), type.name(), recommendedReplicateCount);
//        }
//    }
//}
