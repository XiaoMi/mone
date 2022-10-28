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
//package com.xiaomi.youpin.gwdash.service;
//
//
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import com.google.common.base.Stopwatch;
//import com.google.gson.Gson;
//import com.xiaomi.data.push.client.Pair;
//import com.xiaomi.data.push.redis.Redis;
//import com.xiaomi.youpin.docker.Safe;
//import com.xiaomi.youpin.gwdash.bo.*;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.common.DeployBatchStatusEnum;
//import com.xiaomi.youpin.gwdash.common.HttpUtils;
//import com.xiaomi.youpin.gwdash.common.PipelineStatusEnum;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
//import com.xiaomi.youpin.gwdash.ws.CiCdWebSocketHandler;
//import com.xiaomi.youpin.tesla.agent.po.NotifyMsg;
//import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.Service;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * @author tsingfu
// * @modify zhangzhiyong1
// */
//@Slf4j
//@Service(interfaceClass = DataHubService.class, retries = 0, group = "${dubbo.group}")
//public class DataHubServiceImp implements DataHubService {
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private ProjectDeploymentService projectDeploymentService;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private HealthServiceImp healthServiceImp;
//
//    @Autowired
//    private LogService logService;
//
//    @Autowired
//    private Redis redis;
//
//    public final static String MachineDeployment = "MachineDeployment";
//
//    public final static String DockerDeployment = "DockerDeployment";
//
//    @Autowired
//    private FeiShuService feiShuService;
//
//    @Autowired
//    private ProcessMonitorServiceImp processMonitorServiceImp;
//
//    @Autowired
//    private Environment environment;
//
//    @NacosValue("${grafana.url:}")
//    private String grafanaUrl;
//
//    private ExecutorService pool = Executors.newCachedThreadPool();
//
//    /**
//     * todo: 部署策略实现, 目前实现分批部署
//     * <p>
//     * 物理机&容器部署通用逻辑
//     *
//     * @param dataMessage
//     */
//    @Override
//    public void sendMessage(DataMessage dataMessage) {
//        log.info("receive message: {}", dataMessage);
//        Gson gson = new Gson();
//        NotifyMsg notifyMsg = gson.fromJson(dataMessage.getData(), NotifyMsg.class);
//        log.info("DataHubServiceImp#sendMessage: {}", notifyMsg.getStatus());
//        final long pipelineId = notifyMsg.getBizId();
//        Stopwatch sw = Stopwatch.createStarted();
//        projectDeploymentService.lockLoopRun("DataHubServiceImp", String.valueOf(pipelineId), () -> {
//            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
//            if (null == projectPipeline) {
//                log.info("DataHubServiceImp#sendMessage: projectPipeline is null, {}", pipelineId);
//                return null;
//            }
//            if (projectPipeline.getStatus() == PipelineStatusEnum.CLOSED.getId()) {
//                log.info("DataHubServiceImp#sendMessage: projectPipeline is closed, {}", projectPipeline);
//                return null;
//            }
//            log.info("DataHubServiceImp#sendMessage projectPipeline source: {}", projectPipeline);
//            DeployInfo deployInfo = projectPipeline.getDeployInfo();
//            String kcDeployToken = notifyMsg.getAttachments().get("kc_deploy_token");
//            if (StringUtils.isNotEmpty(kcDeployToken)) {
//                deployInfo.setKcDeployToken(kcDeployToken);
//            }
//            int batch = Integer.parseInt(notifyMsg.getAttachments().get("batch"));
//            String ip = notifyMsg.getAttachments().get("ip");
//            List<DeployBatch> deployBatches = deployInfo.getDeployBatches();
//            int len = deployBatches.size();
//            if (batch >= len || batch < 0) {
//                log.warn("DataHubServiceImp#sendMessage: batch is error, pipeline id: {}, batch: {}", pipelineId, batch);
//                return null;
//            }
//            DeployBatch deployBatch = deployBatches.get(batch);
//            List<DeployMachine> deployMachineList = deployBatch.getDeployMachineList();
//            deployMachineList.stream().filter(it -> ip.equals(it.getIp())).forEach(it -> {
//                if (it.getStatus() != NotifyMsg.STATUS_SUCESSS) {
//                    it.setStep(notifyMsg.getStep());
//                    it.setStatus(notifyMsg.getStatus());
//                    it.setTime(notifyMsg.getTime());
//                }
//            });
//            long total = deployMachineList.size();
//            long failCount = deployMachineList.stream().filter(it -> it.getStatus() == NotifyMsg.STATUS_FAIL).count();
//            long successCount = deployMachineList.stream().filter(it -> it.getStatus() == NotifyMsg.STATUS_SUCESSS).count();
//            if ((failCount + successCount) == total) {
//                deployBatch.setStatus(failCount > 0 ? DeployBatchStatusEnum.PART_SUCCESS.getId() : DeployBatchStatusEnum.ALL_SUCCESS.getId());
//            } else {
//                deployBatch.setStatus(DeployBatchStatusEnum.RUNNING.getId());
//            }
//            int i = dao.update(projectPipeline);
//            log.info("DataHubServiceImp#sendMessage projectPipeline modify: {}, {}", projectPipeline, i);
//            // 判断最后批次整体部署情况
//            int deployBatchStatus = deployBatches.get(len - 1).getStatus();
//
//            //所有批次都处理完会走的流程
//            if (deployBatchStatus == DeployBatchStatusEnum.ALL_SUCCESS.getId()
//                    || deployBatchStatus == DeployBatchStatusEnum.PART_SUCCESS.getId()
//                    || deployBatchStatus == DeployBatchStatusEnum.PART_FAIL.getId()) {
//
//                long count = deployBatches.stream()
//                        .filter(it -> it.getStatus() != DeployBatchStatusEnum.ALL_SUCCESS.getId())
//                        .count();
//                // 完全部署成功逻辑处理
//                if (count <= 0) {
//                    deployInfo.setStep(2);
//                    deployInfo.setStatus(TaskStatus.success.ordinal());
//                    // 关闭当前部署
//                    projectPipeline.setStatus(PipelineStatusEnum.CLOSED.getId());
//                    // 及时保存最新状态，避免开启健康监测导致最新状态被覆盖
//                    dao.update(projectPipeline);
//                    // 物理机 和 容器部署差异处理
//                    ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, projectPipeline.getEnvId());
//                    if (null != projectEnv) {
//                        String msgType = dataMessage.getMsgType();
//                        if (MachineDeployment.equals(msgType)) {
//                            // 物理机健康监测开启
//                            healthServiceImp.startAppHealthCheck(projectEnv);
//                        } else if (DockerDeployment.equals(msgType)) {
//                            int taskId = projectDeploymentService.startHealthCheck(projectEnv.getHealthCheckTaskId(), projectPipeline);
//                            //应用进程监控
//                            Pair<Boolean, Integer> isStart = processMonitorServiceImp.startProcessMonitor(projectEnv.getProcessMonitorTaskId(), projectPipeline);
//                            if (isStart.getKey()) {
//                                projectEnv.setProcessMonitorTaskId(isStart.getValue());
//                                dao.update(projectEnv);
//                            }
//                            log.info("envId:{} deploy finish health check start healthCheckTaskId:{} processMonitorTaskId:{}", projectPipeline.getEnvId(), taskId, isStart.getValue());
//                        }
//                        notifyDeployFinished(projectPipeline.getId(), projectEnv);
//                    }
//                }
//                // 物理机部署下线不在本次部署列表中的机器
//                pool.submit(() -> {
//                    String msgType = dataMessage.getMsgType();
//                    if (MachineDeployment.equals(msgType)) {
//                        projectDeploymentService.offlineLastMachine(projectPipeline);
//                    }
//                });
//
//                if (StringUtils.isNotEmpty(grafanaUrl)) {
//                    List<String> ips = projectPipeline.getDeployInfo().getDeployBatches().stream().flatMap(it -> {
//                        return it.getDeployMachineList().stream().map(m -> m.getIp());
//                    }).collect(Collectors.toList());
//                    pool.submit(() -> {
//                        HttpUtils.post(grafanaUrl, null, "{\"ips\": " + gson.toJson(ips) + ", \"serviceName\": \"" + projectPipeline.getDeploySetting().getServiceName() + "\"}", 1000);
//                    });
//                }
//            }
//
//            pool.submit(() -> Safe.run(() -> {
//                // 向前端推送状态
//                DataMessage message = new DataMessage();
//                message.setMsgType("deploy-update");
//                message.setStage(CiCdWebSocketHandler.DEPLOY_STAGE);
//                message.setData(gson.toJson(projectPipeline));
//                CiCdWebSocketHandler.pushMsg("p" + pipelineId, gson.toJson(message));
//                logService.saveLog(LogService.ProjectDeployment + ip, pipelineId, notifyMsg.getMessage());
//            }));
//            return null;
//        }, 20);
//
//        log.info("sendMessage :{} use time:{}", pipelineId, sw.elapsed(TimeUnit.MILLISECONDS));
//    }
//
//    private void sendFeishuMsg(ProjectEnv projectEnv, String email, String commitMessage, String projectName) {
//        try {
//            StringBuffer sb = new StringBuffer();
//            sb.append("项目：");
//            sb.append(projectName);
//            sb.append(" 部署成功");
//            sb.append("\n部署环境: ");
//            sb.append(projectEnv.getId());
//            sb.append("-");
//            sb.append(projectEnv.getName());
//            sb.append("\n最后提交内容：");
//            sb.append(commitMessage);
//            feiShuService.sendMsg("", sb.toString());
//            feiShuService.sendMessageAsync(email, sb.toString());
//        } catch (Throwable ex) {
//            log.error(ex.getMessage());
//        }
//    }
//
//    private void notifyDeployFinished(Long pipelineId, ProjectEnv projectEnv) {
//        String serverEnv = environment.getProperty("server.serverEnv");
//        if (!"staging".equals(serverEnv) && !"dev".equals(serverEnv)) {
//            return;
//        }
//        // 飞书提示部署成功
//        AutoStartContextDTO context = redis.get(Consts.AUTO_START_PREFIX + "p" + pipelineId, AutoStartContextDTO.class);
//        sendFeishuMsg(projectEnv, (context != null && context.getSessionAccount() != null && Objects.equals(context.getAutoDeploy(), "true")) ? context.getSessionAccount().getEmail() : null, context == null ? "" : context.getCommitMessage(), context == null ? "" : context.getProjectName());
//        redis.del(Consts.AUTO_START_PREFIX + "p" + pipelineId);
//    }
//}
