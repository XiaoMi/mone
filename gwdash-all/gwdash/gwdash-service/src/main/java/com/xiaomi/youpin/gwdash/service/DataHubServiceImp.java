/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.service;


import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.bo.DataMessage;
import com.xiaomi.youpin.gwdash.bo.DeployBatch;
import com.xiaomi.youpin.gwdash.bo.DeployInfo;
import com.xiaomi.youpin.gwdash.bo.DeployMachine;
import com.xiaomi.youpin.gwdash.common.DeployBatchStatusEnum;
import com.xiaomi.youpin.gwdash.common.PipelineStatusEnum;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.tesla.agent.po.NotifyMsg;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tsingfu
 */
@Slf4j
@Service(interfaceClass = DataHubService.class, retries = 0, group = "${dubbo.group}")
public class DataHubServiceImp implements DataHubService {

    @Autowired
    private Dao dao;

    @Autowired
    private ProjectDeploymentService projectDeploymentService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private HealthServiceImp healthServiceImp;

    @Autowired
    private LogService logService;

    public final static String MachineDeployment = "MachineDeployment";

    public final static String DockerDeployment = "DockerDeployment";

    @Autowired
    private FeiShuService feiShuService;

    /**
     * todo: 部署策略实现, 目前实现分批部署
     * <p>
     * 物理机&容器部署通用逻辑
     *
     * @param dataMessage
     */
    @Override
    public synchronized void sendMessage(DataMessage dataMessage) {
        log.info("receive message: {}", dataMessage);
        Gson gson = new Gson();
        NotifyMsg notifyMsg = gson.fromJson(dataMessage.getData(), NotifyMsg.class);
        log.info("DataHubServiceImp#sendMessage: {}", notifyMsg.getStatus());
        final long pipelineId = notifyMsg.getBizId();
        ProjectPipeline projectPipeline0 = pipelineService.getProjectPipelineById(pipelineId).getData();
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, projectPipeline0.getEnvId());
        if (null == projectPipeline0) {
            log.info("DataHubServiceImp#sendMessage: projectPipeline is null, {}", pipelineId);
            return;
        }
        if (projectPipeline0.getStatus() == PipelineStatusEnum.CLOSED.getId()) {
            log.info("DataHubServiceImp#sendMessage: projectPipeline is closed, {}", projectPipeline0);
            return;
        }

        projectDeploymentService.envLockRun("DataHubServiceImp", projectPipeline0.getEnvId(), () -> {
            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
            DeployInfo deployInfo = projectPipeline.getDeployInfo();
            int batch = Integer.valueOf(notifyMsg.getAttachments().get("batch"));
            String ip = notifyMsg.getAttachments().get("ip");
            List<DeployBatch> deployBatches = deployInfo.getDeployBatches();
            int len = deployBatches.size();
            if (batch >= len || batch < 0) {
                log.warn("DataHubServiceImp#sendMessage: batch is error, pipeline id: {}, batch: {}", pipelineId, batch);
                return null;
            }
            DeployBatch deployBatch = deployBatches.get(batch);
            List<DeployMachine> deployMachineList = deployBatch.getDeployMachineList();
            deployMachineList.stream().filter(it -> ip.equals(it.getIp())).forEach(it -> {
                it.setStep(notifyMsg.getStep());
                it.setStatus(notifyMsg.getStatus());
                it.setTime(notifyMsg.getTime());
            });
            long total = deployMachineList.size();
            long failCount = deployMachineList.stream().filter(it -> it.getStatus() == NotifyMsg.STATUS_FAIL).count();
            long successCount = deployMachineList.stream().filter(it -> it.getStatus() == NotifyMsg.STATUS_SUCESSS).count();
            if ((failCount + successCount) == total) {
                deployBatch.setStatus(failCount > 0 ? DeployBatchStatusEnum.PART_SUCCESS.getId() : DeployBatchStatusEnum.ALL_SUCCESS.getId());
            }
            log.info("DataHubServiceImp#sendMessage projectPipeline: {}", projectPipeline);
            int i = dao.update(projectPipeline);
            log.info("DataHubServiceImp#sendMessage projectPipeline: {}, {}", projectPipeline, i);
            // 判断最后批次整体部署情况
            int deployBatchStatus = deployBatches.get(len - 1).getStatus();
            if (deployBatchStatus == DeployBatchStatusEnum.ALL_SUCCESS.getId()
                    || deployBatchStatus == DeployBatchStatusEnum.PART_SUCCESS.getId()
                    || deployBatchStatus == DeployBatchStatusEnum.PART_FAIL.getId()) {

                long count = deployBatches.stream()
                        .filter(it -> it.getStatus() != DeployBatchStatusEnum.ALL_SUCCESS.getId())
                        .count();
                // 完全部署成功逻辑处理
                if (count <= 0) {
                    deployInfo.setStep(2);
                    deployInfo.setStatus(TaskStatus.success.ordinal());
                    // 关闭当前部署
                    projectPipeline.setStatus(PipelineStatusEnum.CLOSED.getId());
                    // 及时保存最新状态，避免开启健康监测导致最新状态被覆盖
                    dao.update(projectPipeline);
                    // 物理机 和 容器部署差异处理
                    if (null != projectEnv) {
                        projectEnv.setPipelineId(projectPipeline.getId());
                        // 及时保存最新状态
                        dao.update(projectEnv);
                        String msgType = dataMessage.getMsgType();
                        if (MachineDeployment.equals(msgType)) {
                            // 物理机健康监测开启
                            healthServiceImp.startAppHealthCheck(projectEnv.getId());
                        } else if (DockerDeployment.equals(msgType)) {
                            int taskId = projectDeploymentService.startHealthCheck(projectEnv.getHealthCheckTaskId(), projectPipeline);
                            log.info("deploy finish health check start taskId:{}", taskId);
                        }
                    }
                    // 飞书提示部署成功
                    sendFeishuMsg(projectEnv);
                }
                // 物理机部署下线不在本次部署列表中的机器
                projectDeploymentService.offlineLastMachine(projectPipeline);
            }
            // 向前端推送状态
            DataMessage message = new DataMessage();
            message.setMsgType("deploy-update");
            message.setData(gson.toJson(projectPipeline));
            pushMsg(pipelineId, gson.toJson(message));
            logService.saveLog(LogService.ProjectDeployment + ip, pipelineId, notifyMsg.getMessage());
            return null;
        }, true, 20);


    }

    private void sendFeishuMsg(ProjectEnv projectEnv) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("应用部署成功");
            sb.append("\n部署环境: ");
            sb.append(projectEnv.getId());
            sb.append("-");
            sb.append(projectEnv.getName());
            feiShuService.sendMsg("", sb.toString());
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

    public static final ConcurrentMap<Long, CopyOnWriteArraySet<WebSocketSession>> subscriber = new ConcurrentHashMap<>();

    private static ReentrantLock reentrantLock = new ReentrantLock(true);

    public static void pushMsg(Long pId, String message) {
        CopyOnWriteArraySet<WebSocketSession> sessions = subscriber.get(pId);
        if (null != sessions) {
            sessions.forEach(it -> {
                reentrantLock.lock();
                try {
                    log.info("push msg :{}", message);
                    it.sendMessage(new TextMessage(message));
                } catch (Throwable e) {
                    log.error("WsSubscriber#pushMsg:" + e.getMessage(), e);
                } finally {
                    reentrantLock.unlock();
                }
            });
        }
    }

    public static void addWebSocketSession(Long pId, WebSocketSession webSocketSession) {
        CopyOnWriteArraySet set = subscriber.get(pId);
        if (null == set) {
            set = new CopyOnWriteArraySet<>();
            set.add(webSocketSession);
            subscriber.put(pId, set);
        } else {
            set.add(webSocketSession);
        }
    }

    public static void removeWebSocketSession(WebSocketSession webSocketSession) {
        Set<Map.Entry<Long, CopyOnWriteArraySet<WebSocketSession>>> set = subscriber.entrySet();
        set.stream().forEach(it -> {
            it.getValue().remove(webSocketSession);
        });
        set = subscriber.entrySet();
        set.stream().forEach(it -> {
            it.getValue().remove(webSocketSession);
        });
    }
}
