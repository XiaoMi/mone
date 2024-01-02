package run.mone.mimeter.agent.manager;

import com.xiaomi.faas.func.api.PrometheusService;
import com.xiaomi.faas.func.domain.MimeterApiRes;
import com.xiaomi.mone.monitor.service.MiFeiShuService;
import com.xiaomi.mone.monitor.service.SendSmsService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.annotation.DubboReference;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import run.mone.mimeter.agent.manager.bo.MibenchTask;
import run.mone.mimeter.agent.manager.bo.SlaContext;
import run.mone.mimeter.dashboard.bo.common.EmitterTypeEnum;
import run.mone.mimeter.dashboard.bo.scene.SceneDTO;
import run.mone.mimeter.dashboard.bo.sla.*;
import run.mone.mimeter.dashboard.service.BenchBroadcastService;
import run.mone.mimeter.dashboard.service.MonitorInfoService;
import run.mone.mimeter.engine.agent.bo.stat.SysMonitorType;
import run.mone.mimeter.engine.agent.bo.task.CancelType;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskType;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static common.Const.*;

@Service
@Slf4j
public class SlaService {

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * 当前sla任务集 <reportId,slaTask>
     */
    private final ConcurrentHashMap<String, SlaContext> slaContextMap = new ConcurrentHashMap<>();

    @Resource
    private ManagerService managerService;

    @DubboReference(check = false, group = "${mimeter.monitor.dubbo.group}", interfaceClass = MiFeiShuService.class, timeout = 3000)
    private MiFeiShuService miFeiShuService;

    @DubboReference(check = false, group = "${mimeter.monitor.dubbo.group}", interfaceClass = SendSmsService.class, timeout = 3000)
    private SendSmsService sendSmsService;
    @DubboReference(check = false, interfaceClass = BenchBroadcastService.class, group = "${mimeter.dashboard.dubbo.group}", version = "${mimeter.dashboard.dubbo.version}", timeout = 20000)
    private BenchBroadcastService benchBroadcastService;

    /**
     * 监控打点数据服务
     */
    @DubboReference(check = false, interfaceClass = PrometheusService.class,timeout = 5000)
    private PrometheusService prometheusService;

    /**
     * 获取trace应用列表服务
     */
    @DubboReference(check = false, group = "${mimeter.dashboard.dubbo.group}", version = "${mimeter.dashboard.dubbo.version}", interfaceClass = MonitorInfoService.class, timeout = 3000)
    private MonitorInfoService monitorInfoService;

    @Resource(name = "$daoName:mibench_st_db", description = "mysql")
    private NutDao dao;

    /**
     * 周期检查时间 10s
     * 即每 10s 核验一次 SLA
     */
    private static final int SCHEDULE = 10;

    public void processSlaNotifyTask(SceneDTO sceneDTO, String reportId) {
        SlaContext slaContext = new SlaContext(reportId, sceneDTO.getId(), false, false);
        slaContextMap.put(reportId, slaContext);
        executorService.submit(() -> {
            try {
                processSlaNotify(sceneDTO, slaContext);
            } catch (InterruptedException e) {
                log.error("processSlaNotify error,reportID:{},error:{}", reportId, e.getMessage());
            }
        });
    }

    /**
     * 校验规则，推送报警通知
     *
     * @param sceneDTO
     */
    private void processSlaNotify(SceneDTO sceneDTO, SlaContext slaContext) throws InterruptedException {

        if (sceneDTO.getSlaDto() == null) {
            return;
        }
        if (sceneDTO.getSceneType() == SCENE_TYPE_HTTP) {
            slaContext.setSceneType(TaskType.http.name());
        } else if (sceneDTO.getSceneType() == SCENE_TYPE_DUBBO) {
            slaContext.setSceneType(TaskType.dubbo.name());
        }
        long begin = System.currentTimeMillis();
        SlaDto slaDto = sceneDTO.getSlaDto();
        List<AlarmDto> alarmDtos = slaDto.getAlarmDtos();
        while (((System.currentTimeMillis() - begin) / 1000) <= (sceneDTO.getBenchTime() + SCHEDULE)) {
            //校验、通知逻辑
            List<SlaRuleDto> rules = slaDto.getSlaRuleDtos();

            rules.forEach(slaRule -> {
                SlaEvent slaEvent = new SlaEvent();
                overRule(slaRule, slaContext, slaEvent, alarmDtos);
            });

            //结束事件通知
            if (slaContext.isCancel() || slaContext.isFinish()) {
                //任务被用户手动停止,退出循环，退出线程 或 任务自然结束,退出循环，退出线程
                log.info("task is cancelled: reportId:{}", slaContext.getReportId());
                clearSlaContext(slaContext.getReportId());
                break;
            }

            TimeUnit.SECONDS.sleep(SCHEDULE);
        }
    }

    private void doAlarm(AlarmDto alarmDto, String content) {
        if (alarmDto != null) {
            alarmDto.getAlarmMethods().forEach(method -> {
                if (method == FEI_SHU_ALARM) {
                    //飞书
                    List<String> users = new ArrayList<>();
                     alarmDto.getUsernames().forEach(userDTO -> {
                         String[] tmpArr = userDTO.getEmail().split("@",2);
                         users.add(tmpArr[0]);
                     });
                    miFeiShuService.batchSendMsg(users, content);
                } else if (method == 1) {
                    //todo 短信
                    sendSmsService.batchSendSms("", "", "");
                }
            });
        }
    }

    /**
     * 是否触发规则
     */
    private void overRule(SlaRuleDto slaRule, SlaContext slaContext, SlaEvent slaEvent, List<AlarmDto> alarmDtos) {
        String ruleType = slaRule.getRuleItemType();
        ConcurrentHashMap<String, AtomicInteger> labelCount = slaContext.getLabelCount();
        if (ruleType.equals(SlaRuleItemTypeEnum.BusinessMetrics.ruleItemTypeName)) {
            Result result = null;
            try {
                result = prometheusService.getMimeterApiInfo(String.valueOf(slaContext.getSceneId()), slaContext.getSceneType());
            } catch (Exception e) {
                log.error("prometheusService call error:{}", e.getMessage());
            }
            if (result == null || result.getCode() != 0 || result.getData() == null) {
                return;
            }
            MimeterApiRes apiMonitorMap = (MimeterApiRes) result.getData();
            log.info("prometheusService call result:{}", result);
            //业务指标
            switch (slaRule.getRuleItem()) {
                case SuccessRate -> {
                    //成功率
                    Map<String, String> successRateMap = apiMonitorMap.getSuccessRate();
                    if (successRateMap != null) {
                        if (checkBusApiMonitorData(successRateMap, slaRule, slaEvent)) {
                            //超过接口成功率规则限制
                            //校验敏感度
                            checkDegree(slaRule, labelCount, slaContext, slaEvent, alarmDtos, SuccessRate);
                        }
                    }
                }
                case P99ResponseTime -> {
                    //p99 rt
                    //成功率
                    Map<String, String> p99Rt = apiMonitorMap.getP99Rt();
                    if (p99Rt != null) {
                        if (checkBusApiMonitorData(p99Rt, slaRule, slaEvent)) {
                            //超过接口成功率规则限制
                            //校验敏感度
                            checkDegree(slaRule, labelCount, slaContext, slaEvent, alarmDtos, P99ResponseTime);
                        }
                    }
                }
                case AvgResponseTime -> {
                    //平均 rt
                    //成功率
                    Map<String, String> avgRt = apiMonitorMap.getAvgRt();
                    if (avgRt != null) {
                        if (checkBusApiMonitorData(avgRt, slaRule, slaEvent)) {
                            //超过接口成功率规则限制
                            //校验敏感度
                            checkDegree(slaRule, labelCount, slaContext, slaEvent, alarmDtos, AvgResponseTime);
                        }
                    }
                }
                case RequestPerSecond -> {
                    //平均 qps
                    //成功率
                    Map<String, String> tps = apiMonitorMap.getTps();
                    if (tps != null) {
                        if (checkBusApiMonitorData(tps, slaRule, slaEvent)) {
                            //超过接口成功率规则限制
                            //校验敏感度
                            checkDegree(slaRule, labelCount, slaContext, slaEvent, alarmDtos, RequestPerSecond);
                        }
                    }
                }
            }
        } else if (ruleType.equals(SlaRuleItemTypeEnum.MonitorMetrics.ruleItemTypeName)) {
            List<String> appList = null;
            try {
                appList = monitorInfoService.getAppListByReportID(slaContext.getSceneId(), slaContext.getReportId());
            } catch (Exception e) {
                log.error("monitorInfoService call error:{}", e.getMessage());
            }
            if (appList == null || appList.size() == 0) {
                //还未串联到应用
                return;
            }
            //监控指标
            switch (slaRule.getRuleItem()) {
                case CpuUtilization -> {
                    //cpu指标
                    if (checkSysMonitor(appList, slaRule, SysMonitorType.Cpu_Usage.name, slaEvent)) {
                        //超过cpu规则限制值
                        checkDegree(slaRule, labelCount, slaContext, slaEvent, alarmDtos, CpuUtilization);
                    }
                }
                case MemoryUtilization -> {
                    //内存指标
                    if (checkSysMonitor(appList, slaRule, SysMonitorType.Mem_Used.name, slaEvent)) {
                        //超过 mem 规则限制值
                        checkDegree(slaRule, labelCount, slaContext, slaEvent, alarmDtos, MemoryUtilization);
                    }
                }
                case Load5Average -> {
                    //load5平均值指标
                    if (checkSysMonitor(appList, slaRule, SysMonitorType.Load_Avg.name, slaEvent)) {
                        //超过平均load规则限制
                        checkDegree(slaRule, labelCount, slaContext, slaEvent, alarmDtos, Load5Average);
                    }
                }
                case Load5Max -> {
                    //load5最大值指标
                    if (checkSysMonitor(appList, slaRule, SysMonitorType.Load_Max.name, slaEvent)) {
                        //超过load5最大值指标
                        checkDegree(slaRule, labelCount, slaContext, slaEvent, alarmDtos, Load5Max);
                    }
                }
            }
        }
    }

    /**
     * 是否超过容忍度
     */
    private void checkDegree(SlaRuleDto slaRule, ConcurrentHashMap<String, AtomicInteger> labelCount, SlaContext slaContext,
                             SlaEvent slaEvent, List<AlarmDto> alarmDtos, String labelName) {
        log.info("checkDegree slaRule:{}", slaEvent);
        //超过规则限制值
        String key = labelName + "_" + slaRule.getAction();
        if (labelCount.containsKey(key)) {
            int currCount = labelCount.get(key).addAndGet(1);
            if (currCount >= slaRule.getDegree()) {
                //超过容忍度，需要处理
                processOverRule(slaRule, slaContext, slaEvent, alarmDtos);
                //处理通知完后清除归零
                labelCount.remove(key);
            }
        } else {
            labelCount.put(key, new AtomicInteger(1));
            if (1 >= slaRule.getDegree()) {
                //超过容忍度，需要处理
                processOverRule(slaRule, slaContext, slaEvent, alarmDtos);
                //处理通知完后清除归零
                labelCount.remove(key);
            }
        }
    }

    /**
     * 基于action，决定发送的事件，以及是否停止压测
     */
    private void processOverRule(SlaRuleDto slaRule, SlaContext slaContext, SlaEvent slaEvent, List<AlarmDto> alarmDtos) {
        log.info("benchBroadcastService.processOverRule alarmDtos:{}", alarmDtos);

        if (alarmDtos == null || alarmDtos.size() == 0) {
            return;
        }
        AlarmDto warnAlarm = null;
        AlarmDto errorAlarm = null;
        for (AlarmDto alarm :
                alarmDtos) {
            if (alarm.getAlarmType().equals(ActionEnum.WARNING.name())) {
                warnAlarm = alarm;
            } else if (alarm.getAlarmType().equals(ActionEnum.ERROR.name())) {
                errorAlarm = alarm;
            }
        }
        switch (slaRule.getAction()) {
            case "WARNING" -> {
                //报警 通知
                slaEvent.setAlarmLevel(slaRule.getAction());
                slaEvent.setCondition(slaRule.getCondition());
                slaEvent.setRuleItem(slaRule.getRuleItem());
                slaEvent.setSlaRuleName(slaRule.getName());
                slaEvent.setDegree(slaRule.getDegree());
                slaEvent.setValue(Double.valueOf(slaRule.getValue()));
                slaEvent.setRuleItemType(slaRule.getRuleItemType());

                String msg = "场景id:" + slaContext.getSceneId() + "压测触发警告规则:" + slaRule.getName();
                slaEvent.setMsg(msg);

                //推送事件流
                try {
                    log.info("benchBroadcastService.notifyEvent slaEvent:{}", slaEvent);
                    benchBroadcastService.notifyEvent(EmitterTypeEnum.SLA_WARN, slaContext.getReportId(), slaEvent);

                    //通知报警接收人
                    doAlarm(warnAlarm, msg);
                } catch (Exception e) {
                    log.error("notify error :{}", e.getMessage());
                }
            }
            case "ERROR" -> {
                //错误 停止
                slaEvent.setAlarmLevel(slaRule.getAction());
                slaEvent.setCondition(slaRule.getCondition());
                slaEvent.setRuleItem(slaRule.getRuleItem());
                slaEvent.setSlaRuleName(slaRule.getName());
                slaEvent.setDegree(slaRule.getDegree());
                slaEvent.setValue(Double.valueOf(slaRule.getValue()));
                slaEvent.setRuleItemType(slaRule.getRuleItemType());

                String msg = "场景id:" + slaContext.getSceneId() + "压测触发错误规则:" + slaRule.getName() + ",即将停止压测";

                slaEvent.setMsg(msg);
                try {
                    //推送事件流
                    benchBroadcastService.notifyEvent(EmitterTypeEnum.SLA_ERROR, slaContext.getReportId(), slaEvent);

                    //通知报警接收人
                    doAlarm(errorAlarm, msg);
                } catch (Exception e) {
                    log.error("notify error :{}", e.getMessage());
                }

                //停止压测
                taskQuitBySlaRule(slaContext.getReportId(), msg);
            }
        }
    }

    private boolean checkBusApiMonitorData(Map<String, String> apiDataMap, SlaRuleDto slaRule, SlaEvent slaEvent) {
        //检验成功率
        log.info("checkBusApiMonitorData apiDataMap:{}", apiDataMap);

        AtomicBoolean ok = new AtomicBoolean(false);
        if (apiDataMap != null) {
            for (Map.Entry<String, String> entry : apiDataMap.entrySet()) {
                if (checkNumValueAndRule(Double.parseDouble(entry.getValue()), slaRule)) {
                    log.info("checkBusApiMonitorData checkNumValueAndRule,value:{}", entry.getValue());
                    //触发的接口id
                    slaEvent.setTriggerApiId(Integer.parseInt(entry.getKey()));
                    slaEvent.setTriggerItem("Api_Id:" + entry.getKey());
                    //该接口的监控值
                    slaEvent.setRuleTargetValue(Double.parseDouble(entry.getValue()));
                    ok.set(true);
                    break;
                }
            }
            return ok.get();
        }
        return true;
    }

    private boolean checkSysMonitor(List<String> appList, SlaRuleDto slaRule, String sysMonitorType, SlaEvent slaEvent) {
        StringBuffer appNameStr = new StringBuffer();
        appList.forEach(appName -> appNameStr.append(",").append(appName));
        Result result = prometheusService.getAppSystemInfo(appNameStr.toString(), sysMonitorType);
        if (result.getCode() != 0 || result.getData() == null) {
            return false;
        }
        Map<String, String> resultMap = (Map<String, String>) result.getData();
        AtomicBoolean match = new AtomicBoolean(false);
        for (Map.Entry<String, String> entry :
                resultMap.entrySet()) {
            if (checkNumValueAndRule((Double.parseDouble(entry.getValue())), slaRule)) {
                match.set(true);
                //触发的接口id
                slaEvent.setTriggerItem("App_Name:" + entry.getKey());
                //该接口的监控值
                slaEvent.setRuleTargetValue(Double.parseDouble(entry.getValue()));
                break;
            }
        }
        return match.get();
    }

    private boolean checkNumValueAndRule(double value, SlaRuleDto slaRule) {
        log.info("checkBusApiMonitorData checkNumValueAndRule,slaRule:{}", slaRule);
        switch (slaRule.getCondition()) {
            case ">" -> {
                if (value > slaRule.getValue()) {
                    return true;
                }
            }
            case "≥" -> {
                if (value >= slaRule.getValue()) {
                    return true;
                }
            }
            case "=" -> {
                if (value == slaRule.getValue()) {
                    return true;
                }
            }
            case "≤" -> {
                if (value <= slaRule.getValue()) {
                    return true;
                }
            }
            default -> {
                return false;
            }
        }
        return false;
    }

    /**
     * 压测任务手动结束需要停止sla任务，移出map
     */
    public void stopSlaTaskByManual(String reportId) {
        SlaContext slaContext = slaContextMap.get(reportId);
        if (slaContext != null) {
            slaContext.setCancel(true);
        }
    }

    /**
     * 压测任务自然结束,需要停止sla任务，移出map
     */
    public void slaTaskFinish(String reportId) {
        SlaContext slaContext = slaContextMap.get(reportId);
        if (slaContext != null) {
            slaContext.setCancel(true);
        }
    }

    /**
     * 基于规则停止压测任务
     */
    private void taskQuitBySlaRule(String reportId, String ruleContent) {
        Task task = new Task();

        List<MibenchTask> mibenchTasks = dao.query(MibenchTask.class, Cnd.where("report_id", "=", reportId));

        List<Integer> taskIds = mibenchTasks.stream().map(MibenchTask::getId).collect(Collectors.toList());
        task.setIds(taskIds);
        task.setCancelType(CancelType.BySla.code);
        task.setCancelBySlaRule(ruleContent);
        managerService.cancelTask(task);

        //清除 内存中的数据
        clearSlaContext(reportId);
    }


    /**
     * 压测任务自然结束、手动结束、sla触发结束，都需要停止sla任务，移出map
     */
    public void clearSlaContext(String reportId) {
        slaContextMap.remove(reportId);
    }
}
