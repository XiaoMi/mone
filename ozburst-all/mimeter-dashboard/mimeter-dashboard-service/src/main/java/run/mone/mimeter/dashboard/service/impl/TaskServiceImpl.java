package run.mone.mimeter.dashboard.service.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.mimeter.dashboard.bo.ChangeQpsReq;
import run.mone.mimeter.dashboard.bo.DubboSceneDebugResult;
import run.mone.mimeter.dashboard.bo.HttpSceneDebugResult;
import run.mone.mimeter.dashboard.bo.SubmitTaskRes;
import run.mone.mimeter.dashboard.bo.common.Constants;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.operationlog.OperationLogDto;
import run.mone.mimeter.dashboard.bo.operationlog.PerOperation;
import run.mone.mimeter.dashboard.bo.report.ReportInfoBo;
import run.mone.mimeter.dashboard.bo.scene.*;
import run.mone.mimeter.dashboard.bo.sceneapi.ApiHeader;
import run.mone.mimeter.dashboard.bo.sceneapi.FormParamValue;
import run.mone.mimeter.dashboard.bo.task.*;
import run.mone.mimeter.dashboard.common.*;
import run.mone.mimeter.dashboard.common.util.BizUtils;
import run.mone.mimeter.dashboard.common.util.Util;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.mapper.*;
import run.mone.mimeter.dashboard.pojo.*;
import run.mone.mimeter.dashboard.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static run.mone.mimeter.dashboard.bo.common.Constants.*;
import static run.mone.mimeter.dashboard.bo.operationlog.OperationLogTypeEnum.DEBUG_OPERATION;
import static run.mone.mimeter.dashboard.bo.operationlog.OperationLogTypeEnum.START_BENCH;
import static run.mone.mimeter.dashboard.bo.operationlog.OperationTypeEnum.DEBUG_RECORD;
import static run.mone.mimeter.dashboard.bo.operationlog.OperationTypeEnum.VIEW_REPORT;
import static run.mone.mimeter.dashboard.bo.snapshot.SnapshotTypeEnum.DEBUG_SNAPSHOT;
import static run.mone.mimeter.dashboard.common.util.Utility.generateId;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final String logPrefix = "[TaskService]";

    @Autowired
    private SceneService sceneService;

    @Autowired
    private FeiShuService feiShuService;

    @Autowired
    private SceneInfoMapper sceneInfoMapper;

    @Autowired
    private MibenchTaskMapper mibenchTaskMapper;

    @Autowired
    private SceneApiInfoMapper sceneApiInfoMapper;

    @Autowired
    private SerialLinkMapper serialLinkMapper;

    @Autowired
    private ReportInfoService reportInfoService;

    @Autowired
    private ReportInfoMapper reportInfoMapper;

    @Autowired
    private ReportEmitterService reportEmitterService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private SceneSnapshotService sceneSnapshotService;

    @Autowired
    private HttpDao httpDao;

    private static final Pattern EL_PATTERN = Pattern.compile("\\{([^}]*)}");

    /**
     * bench manager addr
     */
    @NacosValue(autoRefreshed = true, value = "${bench.api_manager_url}")
    private String apiSvrUrl;

    /**
     * bench allow switch
     * 发布时可控制不允许压测
     */
    @NacosValue(autoRefreshed = true, value = "${bench.allow.switch}")
    private String benchAllowSwitch;

    private static final Gson gson = Util.getGson();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public Result<SubmitTaskRes> submitTask(TaskDTO taskDTO, String opUser) {

        if (!benchAllowSwitch.equalsIgnoreCase("true")){
            return Result.fail(CommonError.SystemDeployingError);
        }
        String reportId = "";
        if (taskDTO.getSubmitTaskType() != SINGLE_API_DEBUG) {
            reportId = generateId((long) taskDTO.getSceneId());
        }
        Pair<Integer, String> checkRes = checkSubmitTaskParam(taskDTO, reportId);
        if (checkRes.getKey() != 0) {
            return Result.fail(CommonError.InvalidParamError.code, checkRes.getValue());
        }
        HttpResult result;

        try {
            result = httpDao.post(apiSvrUrl + "/submit/task", gson.toJson(taskDTO));
        } catch (Exception e) {
            log.error("[TaskService.submitTask] failed to submit task, msg: {}", e.getMessage(), e);
            return Result.fail(CommonError.APIServerError);
        }
        if (result == null) {
            log.error("[TaskService.submitTask] failed to get result from api server, param: {}", taskDTO);
            return Result.fail(CommonError.APIServerError);
        }
        if (result.getCode() == 200) {
            SubmitTaskRes res = gson.fromJson(result.getData(), SubmitTaskRes.class);
            if (taskDTO.getSubmitTaskType() == SCENE_BENCH) {
                SceneInfo sceneInfo = sceneInfoMapper.selectByPrimaryKey(taskDTO.getSceneId());
                if (sceneInfo == null) {
                    return Result.fail(CommonError.InvalidParamError);
                }

                this.reportInfoService.createReport(ReportInfoBo.builder()
                        .sceneId((long) taskDTO.getSceneId())
                        .reportId(reportId)
                        .reportName(genReportName(sceneInfo.getName(), String.valueOf(sceneInfo.getRpsRate())))
                        .createBy(opUser)
                        .tenant(taskDTO.getTenant())
                        .status(REPORT_STATUS_RUNNING)
                        .agents(gson.toJson(res.getAgentIpList()))
                        .linkToDagId(gson.toJson(res.getLinkTaskIdMap()))
                        .build());

                //更新场景当前报告id
                sceneInfo.setCurReportId(reportId);
                //压测次数+1
                sceneInfo.setBenchCount(sceneInfo.getBenchCount() + 1);
                //最近一次压测时间
                long now = System.currentTimeMillis();
                sceneInfo.setLastBenchTime(now);

                //记录压测日历
                recordBenchCalendar(sceneInfo, now);
                sceneInfoMapper.updateByPrimaryKeyWithBLOBs(sceneInfo);

                String finalReportId = reportId;
                //通知负责人
                CompletableFuture.runAsync(() -> notifyPersonsInCharge(sceneInfo, finalReportId, opUser), executorService);

                this.postOperationLog(taskDTO.getSceneId(), SCENE_BENCH, reportId, "", opUser);
            }
            return Result.success(res);
        } else {
            log.error("[TaskService.submitTask], failed and result is: {}", result);
            return Result.fail(result.getCode(), result.getMessage());
        }
    }

    @Override
    public Result<Boolean> stopTask(Integer type, TaskDTO taskDTO, String opUser) {
        taskDTO.setOpUser(opUser);
        Pair<Integer, String> checkRes = checkStopTaskParam(type, taskDTO);
        if (checkRes.getKey() != 0) {
            return Result.fail(CommonError.InvalidParamError.code, checkRes.getValue());
        }
        HttpResult result;
        try {
            List<Integer> taskIds;
            if (type == 1) {
                //场景id
                SceneInfo sceneInfo = sceneInfoMapper.selectByPrimaryKey(taskDTO.getSceneId());
                if (sceneInfo == null) {
                    return Result.fail(CommonError.InvalidParamError.code, "场景不存在");
                }
                if (sceneInfo.getSceneStatus() != TaskStatus.Running.code) {
                    return Result.fail(CommonError.InvalidParamError.code, "场景不在运行状态");
                }
                if (sceneInfo.getCurReportId() == null) {
                    return Result.fail(CommonError.InvalidParamError.code, "任务不存在");
                }
                taskDTO.setReportId(sceneInfo.getCurReportId());
            }
            //报告id
            MibenchTaskExample example = new MibenchTaskExample();
            example.createCriteria().andReportIdEqualTo(taskDTO.getReportId()).andTaskTypeEqualTo(TaskType.dag.code);
            List<MibenchTask> MibenchTasks = mibenchTaskMapper.selectByExample(example);

            //fix temp
            MibenchTasks.forEach(MibenchTask -> {
                MibenchTask.setState(TaskStatus.STOPPED.code);
                mibenchTaskMapper.updateByPrimaryKey(MibenchTask);
            });

            taskIds = MibenchTasks.stream().map(MibenchTask::getId).collect(Collectors.toList());
            taskDTO.setIds(taskIds);

            ReportInfoExample reportInfoExample = new ReportInfoExample();
            reportInfoExample.createCriteria().andReportIdEqualTo(taskDTO.getReportId());
            ReportInfo reportInfo = reportInfoMapper.selectByExample(reportInfoExample).get(0);
            taskDTO.setSceneId(Math.toIntExact(reportInfo.getSceneId()));

            //直接更新为结束状态
            SceneInfo sceneInfo = sceneInfoMapper.selectByPrimaryKey(Math.toIntExact(reportInfo.getSceneId()));
            sceneInfo.setSceneStatus(TaskStatus.STOPPED.code);
            sceneInfoMapper.updateByPrimaryKey(sceneInfo);

            if (taskDTO.getIds().size() == 0) {
                return Result.success(true);
            }
            result = httpDao.post(apiSvrUrl + "/cancel/task", gson.toJson(taskDTO));
        } catch (Exception e) {
            return Result.fail(CommonError.APIServerError);
        }
        if (result == null) {
            log.error("[TaskService.stopTask] failed to get result from api server, param: {}", taskDTO);
            return Result.fail(CommonError.APIServerError);
        }
        if (result.getCode() == 200) {
            return Result.success(true);
        } else {
            log.error("[TaskService.stopTask], failed and result is: {}", result);
            return Result.fail(result.getCode(), result.getMessage());
        }
    }

    @Override
    public Result<Boolean> manualUpdateRps(ChangeQpsReq req) {
        HttpResult result;
        if (req == null || req.getDagTaskRpsList() == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        try {
            result = httpDao.post(apiSvrUrl + "/task/manualUpdateQps", gson.toJson(req));
        } catch (Exception e) {
            return Result.fail(CommonError.APIServerError);
        }
        if (result == null) {
            log.error("[TaskService.manualUpdateRps] failed to get result from api server, param: {}", req);
            return Result.fail(CommonError.APIServerError);
        }
        if (result.getCode() == 200) {
            return Result.success(true);
        } else {
            log.error("[TaskService.manualUpdateRps], failed and result is: {}", result);
            return Result.fail(result.getCode(), result.getMessage());
        }
    }

    @Override
    public Result<Boolean> manualUpdateSceneRpsRate(SceneRpsRateReq req) {
        log.info("manualUpdateSceneRpsRate sceneRpsRateReq:{}", req);
        Pair<Integer, String> checkRes = checkUpdateSceneRateParam(req);
        if (checkRes.getKey() != 0) {
            return Result.fail(CommonError.InvalidParamError.code, checkRes.getValue());
        }
        SceneInfo sceneInfo = sceneInfoMapper.selectByPrimaryKey(req.getSceneID());
        if (sceneInfo == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        ChangeQpsReq changeQpsReq = new ChangeQpsReq();
        changeQpsReq.setRpsRate(req.getRpsRate());
        List<DagTaskRps> dagTaskRpsList = new ArrayList<>();
        changeQpsReq.setDagTaskRpsList(dagTaskRpsList);
        //转换接口压测量信息
        Map<String, LinkRpsInfo> linkMaps = new HashMap<>();
        SerialLinkExample example = new SerialLinkExample();
        example.createCriteria().andSceneIdEqualTo(req.getSceneID());

        //该场景的串联链路集
        List<SerialLink> serialLinkList = serialLinkMapper.selectByExample(example);
        serialLinkList.forEach(serialLink -> {
            if (serialLink.getEnable()) {
                linkMaps.putIfAbsent(serialLink.getName(), new LinkRpsInfo(serialLink.getId()));
            }
        });

        List<ApiBenchInfo> apiBenchInfoList = new ArrayList<>();
        if (Objects.nonNull(sceneInfo.getApiBenchInfos())) {
            apiBenchInfoList = gson.fromJson(sceneInfo.getApiBenchInfos(), new TypeToken<List<ApiBenchInfo>>() {
            }.getType());
        }
        //最大链路rps
        apiBenchInfoList.forEach(apiBenchInfo -> {
            if (linkMaps.containsKey(apiBenchInfo.getSerialName())) {
                linkMaps.get(apiBenchInfo.getSerialName()).setRps(apiBenchInfo.getLinkTps());
            }
        });
        //变更所有任务 rps 量级请求
        linkMaps.values().forEach(linkRpsInfo -> {
            Integer taskId = req.getLinkToTaskMaps().get(linkRpsInfo.getLinkId());
            if (taskId != null) {
                int curRps = (int) Math.ceil(linkRpsInfo.getRps() * (req.getRpsRate() / 100d));
                dagTaskRpsList.add(new DagTaskRps(linkRpsInfo.getLinkId(), taskId, curRps));
            }
        });
        log.debug("manualUpdateSceneRpsRate changeQpsReq:{}", changeQpsReq);
        if (this.manualUpdateRps(changeQpsReq).getData()) {
            //更新报告名
            log.debug("manualUpdateSceneRpsRate changeQpsReq:{}", changeQpsReq);
            CompletableFuture.runAsync(() -> {
                ReportInfoExample reportInfoExample = new ReportInfoExample();
                reportInfoExample.createCriteria().andReportIdEqualTo(req.getReportID());
                ReportInfo reportInfo = reportInfoMapper.selectByExample(reportInfoExample).get(0);
                String curName = reportInfo.getReportName().split("——", 2)[0] + req.getRpsRate() + "%";
                reportInfo.setReportName(curName);
                reportInfoMapper.updateByPrimaryKey(reportInfo);
            });
            return Result.success(true);
        }
        return Result.fail(CommonError.InvalidParamError);
    }

    @Override
    public Result<TreeMap<String, List<HttpSceneDebugResult>>> getHttpSceneDebugResultByTaskId(String reportId, String opUser) {
        if (reportId == null || reportId.isEmpty()) {
            return Result.fail(CommonError.InvalidParamError);
        }
        MibenchTaskExample exp = new MibenchTaskExample();
        exp.createCriteria().andReportIdEqualTo(reportId);
        List<MibenchTask> MibenchTasks = mibenchTaskMapper.selectByExample(exp);
        List<Integer> taskIds = MibenchTasks.stream().map(MibenchTask::getId).collect(Collectors.toList());

        AtomicBoolean record = new AtomicBoolean(true);
        TreeMap<String, List<HttpSceneDebugResult>> resultMap = new TreeMap<>();

        taskIds.forEach(taskId -> {
            final String[] serialLinkName = {""};
            List<HttpSceneDebugResult> resultList = new ArrayList<>();

            MibenchTask dagTag = mibenchTaskMapper.selectByPrimaryKey(taskId);

            HttpSceneDebugResult dagTaskResult = new HttpSceneDebugResult();
            dagTaskResult.setParentTask(true);
            dagTaskResult.setSceneId(dagTag.getSceneId());
            dagTaskResult.setTaskStatus(dagTag.getState());
            resultList.add(dagTaskResult);

            MibenchTaskExample example = new MibenchTaskExample();
            example.createCriteria().andParentTaskIdEqualTo(taskId);
            List<MibenchTask> childTasks = mibenchTaskMapper.selectByExampleWithBLOBs(example);

            childTasks.forEach(childTask -> {
                HttpSceneDebugResult childResult = new HttpSceneDebugResult();
                SceneApiInfo httpApi = sceneApiInfoMapper.selectByPrimaryKey(childTask.getSceneApiId());
                if (serialLinkName[0].isEmpty()) {
                    SerialLink serialLink = serialLinkMapper.selectByPrimaryKey(httpApi.getSerialLinkId());
                    serialLinkName[0] = serialLink.getName();
                }
                BeanUtils.copyProperties(httpApi, childResult);
                childResult.setTaskStatus(childTask.getState());
                if (childTask.getState() != TaskStatus.Success.code) {
                    record.set(false);
                }
                if (childTask.getDebugUrl() != null  && !childTask.getDebugUrl().isEmpty()){
                    childResult.setApiUrl(childTask.getDebugUrl());
                }
                childResult.setApiOrder(httpApi.getApiOrder());
                childResult.setParentTask(false);
                if (childTask.getReqParamType() != null) {
                    childResult.setReqParamType(childTask.getReqParamType());
                }
                if (childTask.getRequestParams() != null && !childTask.getRequestParams().isEmpty()) {
                    childResult.setRealParam(childTask.getRequestParams());
                }
                if (childTask.getDebugResultHeader() != null) {
                    childResult.setRespHeader(childTask.getDebugResultHeader());
                }
                if (childTask.getDebugReqHeaders() != null) {
                    List<ApiHeader> apiHeaders = new ArrayList<>();
                    Map<String, String> reqHeaders = gson.fromJson(childTask.getDebugReqHeaders(), new TypeToken<Map<String, Object>>() {
                    }.getType());
                    reqHeaders.forEach((k, v) -> apiHeaders.add(new ApiHeader(k, v)));
                    childResult.setApiHeader(gson.toJson(apiHeaders));
                }
                if (childTask.getDebugTriggerCp() != null) {
                    childResult.setTriggerCpInfo(childTask.getDebugTriggerCp());
                }
                if (childTask.getDebugTriggerFilterCondition() != null) {
                    childResult.setDebugTriggerFilterCondition(childTask.getDebugTriggerFilterCondition());
                }
                childResult.setOk(childTask.getOk());
                childResult.setDebugResult(childTask.getDebugResult());
                childResult.setRt(childTask.getDebugRt());
                childResult.setSize(childTask.getDebugSize());
                resultList.add(childResult);
            });
            resultMap.put(serialLinkName[0], resultList);
        });

        if (MibenchTasks.size() > 0 && record.get()) {
            this.postOperationLog(MibenchTasks.get(0).getSceneId(), SCENE_DEBUG, reportId, gson.toJson(resultMap), opUser);
        }

        return Result.success(resultMap);
    }

    @Override
    public Result<TreeMap<String, List<DubboSceneDebugResult>>> getDubboSceneDebugResultByTaskId(String reportId, String opUser) {
        if (reportId == null || reportId.isEmpty()) {
            return Result.fail(CommonError.InvalidParamError);
        }
        AtomicBoolean record = new AtomicBoolean(true);

        MibenchTaskExample exp = new MibenchTaskExample();
        exp.createCriteria().andReportIdEqualTo(reportId);
        List<MibenchTask> MibenchTasks = mibenchTaskMapper.selectByExample(exp);
        List<Integer> taskIds = MibenchTasks.stream().map(MibenchTask::getId).collect(Collectors.toList());

        TreeMap<String, List<DubboSceneDebugResult>> resultMap = new TreeMap<>();

        taskIds.forEach(taskId -> {
            final String[] serialLinkName = {""};
            List<DubboSceneDebugResult> resultList = new ArrayList<>();

            MibenchTask dagTag = mibenchTaskMapper.selectByPrimaryKey(taskId);

            DubboSceneDebugResult dagTaskResult = new DubboSceneDebugResult();
            dagTaskResult.setParentTask(true);
            dagTaskResult.setSceneId(dagTag.getSceneId());
            dagTaskResult.setTaskStatus(dagTag.getState());
            resultList.add(dagTaskResult);

            MibenchTaskExample example = new MibenchTaskExample();
            example.createCriteria().andParentTaskIdEqualTo(taskId);
            List<MibenchTask> childTasks = mibenchTaskMapper.selectByExampleWithBLOBs(example);

            childTasks.forEach(childTask -> {
                DubboSceneDebugResult childResult = new DubboSceneDebugResult();
                SceneApiInfo dubboApi = sceneApiInfoMapper.selectByPrimaryKey(childTask.getSceneApiId());
                if (serialLinkName[0].isEmpty()) {
                    SerialLink serialLink = serialLinkMapper.selectByPrimaryKey(dubboApi.getSerialLinkId());
                    serialLinkName[0] = serialLink.getName();
                }
                BeanUtils.copyProperties(dubboApi, childResult);
                childResult.setServiceName(dubboApi.getServiceName());
                childResult.setMethodName(dubboApi.getMethodName());
                childResult.setGroup(dubboApi.getDubboGroup());
                childResult.setVersion(dubboApi.getDubboVersion());

                childResult.setTaskStatus(childTask.getState());
                if (childTask.getState() != TaskStatus.Success.code) {
                    record.set(false);
                }
                childResult.setApiOrder(dubboApi.getApiOrder());
                childResult.setParentTask(false);
                if (dubboApi.getParamTypeList() != null) {
                    childResult.setParamsTypeList(dubboApi.getParamTypeList());
                }
                if (childTask.getRequestParams() != null && !childTask.getRequestParams().isEmpty()) {
                    childResult.setRealParam(childTask.getRequestParams());
                }
                if (childTask.getDebugTriggerCp() != null) {
                    childResult.setTriggerCpInfo(childTask.getDebugTriggerCp());
                }
                if (childTask.getDebugTriggerFilterCondition() != null) {
                    childResult.setDebugTriggerFilterCondition(childTask.getDebugTriggerFilterCondition());
                }
                childResult.setOk(childTask.getOk());
                childResult.setDebugResult(childTask.getDebugResult());
                childResult.setRt(childTask.getDebugRt());
                childResult.setSize(childTask.getDebugSize());
                resultList.add(childResult);
            });
            resultMap.put(serialLinkName[0], resultList);
        });

        if (MibenchTasks.size() > 0 && record.get()) {
            this.postOperationLog(MibenchTasks.get(0).getSceneId(), SCENE_DEBUG, reportId, gson.toJson(resultMap), opUser);
        }

        return Result.success(resultMap);
    }

    @Override
    public SseEmitter stream(String reportId, String username) {
        return this.reportEmitterService.connect(reportId, username);
    }

    private void postOperationLog(Integer sceneId, int type, String reportId, String snapshotRes, String opUser) {
        try {
            OperationLogDto opRecord = new OperationLogDto();
            opRecord.setSceneId(sceneId);
            opRecord.setCreateBy(opUser);
            List<PerOperation> supportOperations = new ArrayList<>();
            opRecord.setSupportOperation(supportOperations);

            if (type == SCENE_DEBUG) {
                String snapshotId = this.sceneSnapshotService.createSnapshot(SceneSnapshotBo.builder()
                        .sceneId((long) sceneId)
                        .type(DEBUG_SNAPSHOT.typeCode)
                        .createBy(opUser)
                        .scene(snapshotRes)
                        .build()).getData();
                opRecord.setType(DEBUG_OPERATION.typeCode);
                opRecord.setContent("调试场景");
                opRecord.setReportId(reportId);
                PerOperation perOperationDebugRecord = new PerOperation(DEBUG_RECORD.typeCname, DEBUG_RECORD.typeName, snapshotId);
                supportOperations.add(perOperationDebugRecord);
            } else if (type == SCENE_BENCH) {
                opRecord.setType(START_BENCH.typeCode);
                opRecord.setContent("启动压测");
                PerOperation perOperationViewReport = new PerOperation(VIEW_REPORT.typeCname, VIEW_REPORT.typeName, reportId);
                supportOperations.add(perOperationViewReport);
            }
            operationLogService.newOperationLog(opRecord);
        } catch (Exception e) {
            log.error("TaskServiceImpl.postOperationLog, error msg: {}", e.getMessage(), e);
        }
    }

    /**
     * 通知负责人
     */
    private void notifyPersonsInCharge(SceneInfo sceneInfo, String reportId, String opUser) {
        //场景负责人
        List<String> personsInCharge = gson.fromJson(sceneInfo.getPersonInCharge(), new TypeToken<List<String>>() {
        }.getType());

        Map<String, String> info = new HashMap<>();
        info.put("sceneName", sceneInfo.getName());
        info.put("opUser", opUser);
        info.put("benchTime", String.valueOf(sceneInfo.getBenchTime()));
        info.put("totalRps", String.valueOf(sceneInfo.getMaxBenchQps() * (sceneInfo.getRpsRate() / 100d)));
        info.put("reportId", String.valueOf(reportId));

        String content = TemplateUtils.processTemplate(BENCH_BEGIN_MSG, info);
        try {
            feiShuService.sendCard2Person(personsInCharge, content);
        } catch (Exception e) {
            log.error("notifyPersonsInCharge error:{}", e.getMessage());
        }
    }

    /**
     * 记录压测日历
     */
    private void recordBenchCalendar(SceneInfo sceneInfo, long now) {
        BenchCalendar benchCalendar;
        if (sceneInfo.getBenchCalendar() == null || sceneInfo.getBenchCalendar().isEmpty()) {
            benchCalendar = new BenchCalendar();
            benchCalendar.setBenchDateList(new ArrayList<>());
        } else {
            benchCalendar = gson.fromJson(sceneInfo.getBenchCalendar(), BenchCalendar.class);
        }
        List<BenchDate> tmpList = new ArrayList<>();
        //过滤一年前的记录
        benchCalendar.getBenchDateList().forEach(benchDate -> {
            if (now - benchDate.getTimestamp() <= ONE_YEAR_MS) {
                tmpList.add(benchDate);
            }
        });
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String nowDate = date.format(formatter);
        boolean find = false;
        for (BenchDate value : tmpList) {
            if (nowDate.equals(value.getBenchDate())) {
                find = true;
                value.setDateBenchCount(value.getDateBenchCount() + 1);
                break;
            }
        }
        //没找到，追加
        if (!find) {
            BenchDate benchDate = new BenchDate();
            benchDate.setBenchDate(nowDate);
            benchDate.setTimestamp(now);
            benchDate.setDateBenchCount(1);
            tmpList.add(benchDate);
        }
        benchCalendar.setBenchDateList(tmpList);
        sceneInfo.setBenchCalendar(gson.toJson(benchCalendar));
    }


    /**
     * 检查校验场景比例请求数据
     */
    private Pair<Integer, String> checkUpdateSceneRateParam(SceneRpsRateReq req) {

        if (req.getSceneID() == null || req.getSceneID() == 0) {
            return Pair.of(-1, "无效的场景id");
        }
        if (req.getReportID() == null || req.getReportID().isEmpty()) {
            return Pair.of(-1, "无效的报告id");
        }
        if (req.getRpsRate() > 100 || req.getRpsRate() < 0) {
            return Pair.of(-1, "无效的发压比例");
        }
        return Pair.of(0, "success");
    }

    private Pair<Integer, String> checkStopTaskParam(int type, TaskDTO taskDTO) {

        if (type == 0 && taskDTO.getReportId() == null) {
            return Pair.of(-1, "报告id必传");
        }

        if (type == 1 && taskDTO.getSceneId() == null) {
            return Pair.of(-1, "场景id必传");
        }
        return Pair.of(0, "success");
    }

    private Pair<Integer, String> checkSubmitTaskParam(TaskDTO taskDTO, String reportId) {

        if (taskDTO.getSubmitTaskType() == null) {
            return Pair.of(-1, "任务类型必填");
        }
        switch (taskDTO.getSubmitTaskType()) {
            case SINGLE_API_DEBUG:
                return checkSingleDebugParam(taskDTO);
            case SCENE_DEBUG:
                return checkSceneDebugParam(taskDTO, reportId);
            case SCENE_BENCH:
                return checkSceneBenchParam(taskDTO, reportId);
            default:
                return Pair.of(-1, "不支持的任务类型");
        }
    }

    private Pair<Integer, String> checkSingleDebugParam(TaskDTO taskDTO) {
        DebugSceneApiInfoReq apiInfo = taskDTO.getApiInfo();

        if (null == apiInfo) {
            return Pair.of(-1, "调试接口参数必传");
        }
        if (null == apiInfo.getApiType()) {
            return Pair.of(-1, "接口类型必传");
        }
        if (apiInfo.getApiType() == Constants.HTTP_API_TYPE) {
            if (null == apiInfo.getApiUrl()) {
                return Pair.of(-1, "接口 url 必传");
            }
            if (null == apiInfo.getRequestMethod()) {
                return Pair.of(-1, "接口请求方式必传");
            }
        }
        if (apiInfo.getApiType() == DUBBO_API_TYPE) {
            if (null == apiInfo.getServiceName()) {
                return Pair.of(-1, "dubbo接口 服务名 必传");
            }
            if (null == apiInfo.getMethodName()) {
                return Pair.of(-1, "dubbo接口 方法名必传");
            }
            if (null == apiInfo.getDubboGroup()) {
                apiInfo.setDubboGroup("");
            }
            if (null == apiInfo.getDubboVersion()) {
                apiInfo.setDubboVersion("");
            }
            if (null == apiInfo.getDubboParamJson()) {
                apiInfo.setDubboParamJson("");
            }
            if (null == apiInfo.getRequestBody()) {
                apiInfo.setRequestBody("");
            }
        }

        if (null == apiInfo.getRequestTimeout()) {
            return Pair.of(-1, "请求超时时间必传");
        }

        if (apiInfo.getApiType() == HTTP_API_TYPE && apiInfo.getRequestMethod() == HTTP_REQ_GET) {
            Pair<Integer, List<FormParamValue>> res = parseGetUrl(apiInfo.getApiUrl());
            if (res.getLeft() == -1) {
                return Pair.of(-1, "请求url格式错误");
            } else {
                apiInfo.setRequestBody(gson.toJson(res.getRight()));
                if (apiInfo.getApiUrl().contains("?")) {
                    apiInfo.setApiUrl(apiInfo.getApiUrl().split("\\?")[0]);
                }
            }
        }
        if (apiInfo.getCheckPointInfoList() != null) {
            apiInfo.setCheckPointInfoListStr(gson.toJson(apiInfo.getCheckPointInfoList()));
        }
        //转换解析表达式
        if (apiInfo.getOutputParamInfos() != null) {
            BizUtils.processOutputParamExpr(apiInfo.getOutputParamInfos());
            apiInfo.setOutputParamInfosStr(gson.toJson(apiInfo.getOutputParamInfos()));
        }

        return Pair.of(0, "success");
    }

    private Pair<Integer, String> checkSceneDebugParam(TaskDTO taskDTO, String reportId) {
        if (null == taskDTO.getSceneId()) {
            return Pair.of(-1, "场景id必传");
        }
        if (sceneService.getSceneByID(taskDTO.getSceneId(), false).getData() == null) {
            return Pair.of(-1, "场景id不存在");
        }
        taskDTO.setReportId(reportId);
        return Pair.of(0, "success");
    }

    private Pair<Integer, String> checkSceneBenchParam(TaskDTO taskDTO, String reportId) {
        //若正在运行，驳回
        MibenchTaskExample example = new MibenchTaskExample();
        example.createCriteria().andSceneIdEqualTo(taskDTO.getSceneId()).andStateEqualTo(TaskStatus.Running.code);
        List<MibenchTask> MibenchTasks = mibenchTaskMapper.selectByExample(example);
        if (MibenchTasks.size() != 0) {
            return Pair.of(-1, "该场景有正在执行的压测任务");
        }
        SceneDTO sceneDTO = sceneService.getSceneByID(taskDTO.getSceneId(), false).getData();
        if (sceneDTO == null) {
            return Pair.of(-1, "场景id不存在");
        }
        Set<String> enableSerialSet = new HashSet<>();
        sceneDTO.getSerialLinkDTOs().forEach(serialLinkDTO -> {
            if (serialLinkDTO.getEnable()) {
                enableSerialSet.add(serialLinkDTO.getSerialLinkName());
            }
        });
        if (enableSerialSet.size() == 0) {
            return Pair.of(-1, "至少需要启用一条链路");
        }

        //rps 模式
        if (sceneDTO.getBenchMode() == BenchModeEnum.RPS.code && sceneDTO.getIncrementMode() == BenchIncreaseModeEnum.PERCENT_INCREASE.code) {
            if (sceneDTO.getBenchTime() < 60) {
                //百分比递增模式不能小于1分钟
                return Pair.of(-1, "百分比递增模式压测时间不得小于一分钟");
            }
            for (ApiBenchInfo apiBenchInfo :
                    sceneDTO.getApiBenchInfos()) {
                if (enableSerialSet.contains(apiBenchInfo.getSerialName()) && apiBenchInfo.getMaxRps() - apiBenchInfo.getOriginRps() < 100) {
                    return Pair.of(-1, "百分比递增模式压测链路Rps差值不得小于 100");
                }
            }
        }
        taskDTO.setReportId(reportId);
        return Pair.of(0, "success");
    }

    private Pair<Integer, List<FormParamValue>> parseGetUrl(String url) {
        //url 例: http://www.baidu.com?a=b&b=2
        List<FormParamValue> kvPairs = new ArrayList<>();
        if (url.contains("?")) {
            String[] urlArr = url.split("\\?", 2);
            if (urlArr.length != 2) {
                return Pair.of(-1, null);
            }
            //[a=b,b=2]
            String[] kvStrPair = urlArr[1].split("&");
            for (int i = 0; i < kvStrPair.length; i++) {
                String[] kAndV = kvStrPair[i].split("=", 2);
                kvPairs.add(new FormParamValue(kAndV[0], kAndV[1]));
            }
        }
        return Pair.of(0, kvPairs);
    }

    private String genReportName(String sceneName, String rpsRate) {
        return sceneName + "——" + rpsRate + "%";
    }

}
