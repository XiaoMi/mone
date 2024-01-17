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

package run.mone.mimeter.agent.manager.processor;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import common.Util;
import io.netty.channel.ChannelHandlerContext;
import org.apache.dubbo.annotation.DubboReference;
import org.nutz.dao.impl.NutDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.mimeter.agent.manager.DataStatService;
import run.mone.mimeter.agent.manager.ManagerService;
import run.mone.mimeter.agent.manager.SlaService;
import run.mone.mimeter.agent.manager.bo.MibenchTask;
import run.mone.mimeter.dashboard.bo.common.EmitterTypeEnum;
import run.mone.mimeter.dashboard.bo.report.ReportInfoBo;
import run.mone.mimeter.dashboard.service.BenchBroadcastService;
import run.mone.mimeter.dashboard.service.SceneInfoService;
import run.mone.mimeter.engine.agent.bo.MibenchCmd;
import run.mone.mimeter.engine.agent.bo.data.AgentReq;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.task.CancelType;
import run.mone.mimeter.engine.agent.bo.task.TaskResult;
import run.mone.mimeter.engine.agent.bo.task.TaskStatus;
import run.mone.mimeter.engine.agent.bo.task.TaskStatusBo;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @author dongzhenxing
 * @date 2022/5/11
 * 用来处理agent回推的结果
 */
@Component
public class ManagerProcessor implements NettyRequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(ManagerProcessor.class);

    private static final String logPrefix = "[ManagerProcessor]";

    private final Gson gson = Util.getGson();

    @Resource(name = "$daoName:mibench_st_db", description = "mysql")
    private NutDao dao;

    @DubboReference(check = false, group = "${mimeter.dashboard.dubbo.group}", version = "${mimeter.dashboard.dubbo.version}", interfaceClass = SceneInfoService.class, timeout = 3000)
    private SceneInfoService sceneInfoService;

    @DubboReference(check = false, group = "${mimeter.dashboard.dubbo.group}", version = "${mimeter.dashboard.dubbo.version}", interfaceClass = BenchBroadcastService.class, timeout = 20000)
    private BenchBroadcastService benchBroadcastService;

    /**
     * 数据统计服务
     */
    @Resource
    private DataStatService dataStatService;

    /**
     * 记录场景单次压测完成的任务数
     * <"reportId",4>
     */
    private final ConcurrentHashMap<String, AtomicInteger> reportFinishNum = new ConcurrentHashMap<>();

    /**
     * 记录每个任务执行完成的发压机数量
     * <"taskId",4>
     */
    private final ConcurrentHashMap<Integer, AtomicInteger> taskFinishAgentNum = new ConcurrentHashMap<>();


    @Resource
    private SlaService slaService;

    @Resource
    private ManagerService managerService;

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws InterruptedException {
        AgentReq req = remotingCommand.getReq(AgentReq.class);
        String logMsg = logPrefix + "processRequest ";
        switch (req.getCmd()) {
            case AgentReq.TASK_RESULT_CMD ->//处理完成返回结果事件
                    processTREvent(req, logMsg);
            case AgentReq.TOTAL_DATA_COUNT_CMD ->//处理数据统计事件
                    dataStatService.processTotalCountCtxEvent(req);
            case AgentReq.UP_TASK_STATUS ->//处理任务状态变更事件
                    processUpStatusEvent(req, logMsg);
        }
        return null;
    }


    private synchronized void processTREvent(AgentReq req, String logMsg) {
        //处理回调过来的返回数据
        TaskResult tr = req.getTaskResult();
        try {
            MibenchTask task = dao.fetch(MibenchTask.class, tr.getId());
            //场景调试的情况
            if (tr.isDebug()) {
                // 更新场景接口结果数据  调用 dashboard 接口，更新scene api info debug result
                if (tr.getSuccess().get() == 1) {
                    task.setState(TaskStatus.Success.code);
                } else if (tr.getFailure().get() == 1) {
                    task.setState(TaskStatus.Failure.code);
                }
                task.setOk(tr.isOk());
                CommonReqInfo commonReqInfo = tr.getCommonReqInfo();
                if (commonReqInfo != null) {
                    task.setReqParamType(commonReqInfo.getParamsType());
                    task.setRequestParams(commonReqInfo.getParamJson());
                    if (commonReqInfo.getHeaders() != null) {
                        task.setDebugReqHeaders(gson.toJson(commonReqInfo.getHeaders()));
                    }
                    if (commonReqInfo.getDebugUrl() != null){
                        task.setDebugUrl(commonReqInfo.getDebugUrl());
                    }
                }
                task.setDebugResult(tr.getResult());
                if (tr.getRespHeaders() != null) {
                    task.setDebugResultHeader(gson.toJson(tr.getRespHeaders()));
                }
                if (tr.getTriggerCpInfo() != null) {
                    task.setDebugTriggerCp(tr.getTriggerCpInfo());
                }
                if (tr.getTriggerFilterCondition() != null){
                    task.setDebugTriggerFilterCondition(tr.getTriggerFilterCondition());
                }
                task.setDebugRt((int) tr.getRt());
                task.setDebugSize((int) tr.getSize());
                dao.update(task);
            } else {
                //执行压测的情况下
                String quitMsg = "";
                int finNum = countTaskFinAgent(task.getId());
                if (checkBenchTaskFinish(finNum, task.getAgentNum())) {
                    //即该链路任务全部执行结束
                    task.setState(TaskStatus.Success.code);
                    //结束链路任务状态
                    dao.update(task);
                    //手动结束 需要通知结束报告
                    if (tr.isQuitByManual()) {
                        //手动的需要通知sla任务停止
                        if (tr.getCancelType() == CancelType.Manual.code) {
                            slaService.stopSlaTaskByManual(tr.getReportId());
                            quitMsg = "当前压测任务已手动停止,操作人: " + tr.getOpUser() + ",停止时间: " + getDateTime(System.currentTimeMillis());
                        }
                        if (tr.getCancelType() == CancelType.BySla.code) {
                            slaService.slaTaskFinish(tr.getReportId());
                            quitMsg = "当前压测任务触发SLA警报已停止,触发规则: " + tr.getCancelBySlaRule() + ",停止时间: " + getDateTime(System.currentTimeMillis());
                        }
                    } else {
                        //自然结束
                        //更新场景状态
                        sceneInfoService.updateSceneStatus(tr.getSceneId(), TaskStatus.Success.code);

                        slaService.slaTaskFinish(tr.getReportId());
                        quitMsg = "当前压测任务已完成,结束时间: " + getDateTime(System.currentTimeMillis());
                    }
                    //检查整个场景全部任务是否完成
                    int finishTaskNum;
                    if (reportFinishNum.containsKey(task.getReportId())) {
                        finishTaskNum = reportFinishNum.get(task.getReportId()).incrementAndGet();
                    } else {
                        reportFinishNum.put(task.getReportId(), new AtomicInteger(1));
                        finishTaskNum = reportFinishNum.get(task.getReportId()).get();
                    }
                    if (finishTaskNum >= task.getConnectTaskNum()) {
                        //本次压测所有任务完成再通知dashboard
                        try {
                            this.notifyFinish(task, quitMsg);
                        } catch (Exception e) {
                            log.error(logMsg + "======FAILED TO NOTIFY FINISH!", e);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error(logMsg + " throws error cmd " + req.getCmd() + "; " + ex.getMessage());
        }
    }

    private void processUpStatusEvent(AgentReq req, String logMsg) {
        //更新状态
        try {
            TaskStatusBo statusBo = req.getStatusBo();
            log.info("processUpStatusEvent task id :{}", statusBo.getTaskId());
            MibenchTask dagTask = dao.fetch(MibenchTask.class, statusBo.getTaskId());
            log.info("processUpStatusEvent dag task :{}", dagTask);
            if (dagTask == null){
                Thread.sleep(500);
                dagTask = dao.fetch(MibenchTask.class, statusBo.getTaskId());
            }
            if (dagTask == null){
                log.error("processUpStatusEvent dag is null");
                return;
            }
            dagTask.setState(statusBo.getTaskStatus().code);
            dagTask.setUtime(System.currentTimeMillis());
            int v = dao.update(dagTask);
            if (v > 0) {
                if (statusBo.getTaskStatus() == TaskStatus.Stopped) {
                    //已经停止,更新场景状态为 停止
                    sceneInfoService.updateSceneStatus(statusBo.getSceneId(), TaskStatus.Stopped.code);
                }else if (statusBo.getTaskStatus() == TaskStatus.Running){
                    //任务启动
                    log.info("taskRunningNotify in bench mode:{},increase mode :{},dagTask increasePercent:{}", dagTask.getBenchMode(),dagTask.getIncreaseMode(),dagTask.getIncreasePercent());
                    managerService.taskRunningNotify(dagTask);
                }
                log.info(logMsg + "update task cmd " + req.getCmd());
            }
        } catch (Exception ex) {
            log.error(logMsg + " throws error cmd " + req.getCmd() + "; " + ex.getMessage());
        }
    }

    /**
     * 累计该任务执行完成的压测机数量
     */
    private int countTaskFinAgent(int taskId) {
//        synchronized (taskFinishAgentNum) {
        if (!taskFinishAgentNum.containsKey(taskId)) {
            taskFinishAgentNum.put(taskId, new AtomicInteger(1));
            return 1;
        } else {
            return taskFinishAgentNum.get(taskId).incrementAndGet();
        }
//        }
    }

    private static boolean checkBenchTaskFinish(int finNum, int useAgentNum) {
        return finNum >= useAgentNum;
    }

    private void notifyFinish(MibenchTask task, String quitMsg) {
        //扣除前期大概的初始化时间
        long now = System.currentTimeMillis() - 500;
        this.benchBroadcastService.notifyEvent(EmitterTypeEnum.FINISH, task.getReportId(), ReportInfoBo.builder()
                .sceneId((long) task.getSceneId())
                .reportId(task.getReportId())
                .duration((int) ((now - task.getCtime()) / 1000))
                .finishTime(now)
                .extra(quitMsg)
                .build());
    }

    private String getDateTime(long now) {
        Date millisecondDate = new Date(now);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(millisecondDate);
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int cmdId() {
        return MibenchCmd.MANAGER;
    }
}
