package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.ChangeQpsReq;
import run.mone.mimeter.dashboard.bo.DubboSceneDebugResult;
import run.mone.mimeter.dashboard.bo.HttpSceneDebugResult;
import run.mone.mimeter.dashboard.bo.SubmitTaskRes;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.task.SceneRpsRateReq;
import run.mone.mimeter.dashboard.bo.task.TaskDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.TreeMap;

public interface TaskService {

    Result<SubmitTaskRes> submitTask(TaskDTO taskDTO, String opUser);

    Result<Boolean> stopTask(Integer type,TaskDTO taskDTO,String opUser);

    Result<Boolean> manualUpdateRps(ChangeQpsReq req);

    Result<Boolean> manualUpdateSceneRpsRate(SceneRpsRateReq req);

    Result<TreeMap<String,List<HttpSceneDebugResult>>> getHttpSceneDebugResultByTaskId(String reportId,  String opUser);

    Result<TreeMap<String,List<DubboSceneDebugResult>>> getDubboSceneDebugResultByTaskId(String reportId, String opUser);

    SseEmitter stream(String reportId, String username);
}
