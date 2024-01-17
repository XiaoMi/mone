package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.mimeter.dashboard.bo.ChangeQpsReq;
import run.mone.mimeter.dashboard.bo.DubboSceneDebugResult;
import run.mone.mimeter.dashboard.bo.HttpSceneDebugResult;
import run.mone.mimeter.dashboard.bo.SubmitTaskRes;
import run.mone.mimeter.dashboard.bo.common.Constants;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.task.DebugSceneApiInfoReq;
import run.mone.mimeter.dashboard.bo.task.SceneRpsRateReq;
import run.mone.mimeter.dashboard.bo.task.TaskDTO;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.TaskService;
import run.mone.mimeter.dashboard.service.impl.LoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
@RestController
@RequestMapping(value = "/api/bench/task")
@HttpApiModule(value = "TaskController", apiController = TaskController.class)
public class TaskController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private TaskService taskService;

    /**
     * 单接口测试任务
     *
     */
    @HttpApiDoc(value = "/api/bench/task/submitSingleApiDebugTask", apiName = "提交执行单接口测试任务", method = MiApiRequestMethod.POST, description = "提交接口测试执行任务")
    @RequestMapping(value = "/submitSingleApiDebugTask", method = RequestMethod.POST)
    public Result<SubmitTaskRes> submitSingleApiDebugTask(
            HttpServletRequest request,
            @RequestBody DebugSceneApiInfoReq apiInfoReq
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.submitSingleApiDebugTask] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setSubmitTaskType(Constants.SINGLE_API_DEBUG);
        taskDTO.setApiInfo(apiInfoReq);
        taskDTO.setTenant(account.getTenant());
        return taskService.submitTask(taskDTO, account.getUsername());
    }

    /**
     * 执行场景调试任务
     *
     * @param request
     * @param
     * @return
     */
    @HttpApiDoc(value = "/api/bench/task/submitSceneDebugTask", apiName = "执行场景调试任务", method = MiApiRequestMethod.POST, description = "执行场景调试任务")
    @RequestMapping(value = "/submitSceneDebugTask", method = RequestMethod.POST)
    public Result<SubmitTaskRes> submitSceneDebugTask(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "sceneID", description = "场景id", required = true, defaultValue = "234")
            Integer sceneID
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.submitSceneDebugTask] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setSceneId(sceneID);
        taskDTO.setSubmitTaskType(Constants.SCENE_DEBUG);
        taskDTO.setTenant(account.getTenant());

        return taskService.submitTask(taskDTO, account.getUsername());
    }

    /**
     * 执行场景压测任务
     *
     * @param request
     * @param
     * @return
     */
    @HttpApiDoc(value = "/api/bench/task/submitSceneBenchTask", apiName = "执行场景压测任务", method = MiApiRequestMethod.POST, description = "执行场景压测任务")
    @RequestMapping(value = "/submitSceneBenchTask", method = RequestMethod.POST)
    public Result<SubmitTaskRes> submitSceneBenchTask(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "sceneID", description = "场景id", required = true, defaultValue = "234")
            Integer sceneID) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.submitSceneBenchTask] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setSceneId(sceneID);
        taskDTO.setSubmitTaskType(Constants.SCENE_BENCH);
        taskDTO.setTenant(account.getTenant());

        return taskService.submitTask(taskDTO, account.getUsername());
    }

    @HttpApiDoc(value = "/api/bench/task/stopTask", apiName = "停止任务", method = MiApiRequestMethod.POST, description = "停止任务")
    @RequestMapping(value = "/stopTask", method = RequestMethod.POST)
    public Result<Boolean> stopTask(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "type", required = true, description = "基于哪个id停止任务 0:报告id 1:场景", defaultValue = "1")
            Integer type,
            @HttpApiDocClassDefine(value = "sceneID", required = true, description = "场景ID", defaultValue = "66")
            Integer sceneID,
            @HttpApiDocClassDefine(value = "reportID", required = true, description = "报告ID", defaultValue = "66")
            String reportID
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.stopTask] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setReportId(reportID);
        taskDTO.setSceneId(sceneID);
        taskDTO.setTenant(account.getTenant());

        return taskService.stopTask(type,taskDTO, account.getUsername());
    }

    @HttpApiDoc(value = "/api/bench/task/manualUpdateRps", apiName = "手动调节链路rps", method = MiApiRequestMethod.POST, description = "手动调节链路rps")
    @RequestMapping(value = "/manualUpdateRps", method = RequestMethod.POST)
    public Result<Boolean> manualUpdateRps(
            HttpServletRequest request,
            @RequestBody ChangeQpsReq req
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.stopTask] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return taskService.manualUpdateRps(req);
    }

    @HttpApiDoc(value = "/api/bench/task/manualUpdateSceneRpsRate", apiName = "实时手动调节整个场景发压比例", method = MiApiRequestMethod.POST, description = "手动调节链路rps")
    @RequestMapping(value = "/manualUpdateSceneRpsRate", method = RequestMethod.POST)
    public Result<Boolean> manualUpdateSceneRpsRate(
            HttpServletRequest request,
            @RequestBody SceneRpsRateReq req){
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.manualUpdateSceneRpsRate] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return taskService.manualUpdateSceneRpsRate(req);
    }

    @HttpApiDoc(value = "/api/bench/task/getHttpSceneDebugResultByTaskId", apiName = "获取场景调试结果数据", method = MiApiRequestMethod.POST, description = "获取场景调试结果数据")
    @RequestMapping(value = "/getHttpSceneDebugResultByTaskId", method = RequestMethod.POST)
    public Result<TreeMap<String, List<HttpSceneDebugResult>>> getSceneDebugResultByTaskId(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "reportID", required = true, description = "该任务id", defaultValue = "66")
            String reportID) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.getSceneDebugResultByTaskId] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return taskService.getHttpSceneDebugResultByTaskId(reportID, account.getUsername());
    }

    @HttpApiDoc(value = "/api/bench/task/getDubboSceneDebugResultByTaskId", apiName = "获取场景调试结果数据", method = MiApiRequestMethod.POST, description = "获取场景调试结果数据")
    @RequestMapping(value = "/getDubboSceneDebugResultByTaskId", method = RequestMethod.POST)
    public Result<TreeMap<String, List<DubboSceneDebugResult>>> getDubboSceneDebugResultByTaskId(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "reportID", required = true, description = "该任务id", defaultValue = "66")
            String reportID) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.getDubboSceneDebugResultByTaskId] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return taskService.getDubboSceneDebugResultByTaskId(reportID, account.getUsername());
    }

    @HttpApiDoc(value = "/api/bench/task/stream", apiName = "连接实时压测", method = MiApiRequestMethod.GET, description = "连接实时压测")
    @RequestMapping(value = "/stream", method = RequestMethod.GET, produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter stream(HttpServletRequest request,
                             HttpServletResponse response,
                             @HttpApiDocClassDefine(value = "reportId", required = true)
                             @RequestParam String reportId) throws IllegalAccessException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.stream] current user not have valid account info in session");
            throw new IllegalAccessException("用户未登录");
        }
        response.addHeader("X-Accel-Buffering", "no");
        response.addHeader("Cache-Control","false");
        return this.taskService.stream(reportId, account.getUsername());
    }
}
