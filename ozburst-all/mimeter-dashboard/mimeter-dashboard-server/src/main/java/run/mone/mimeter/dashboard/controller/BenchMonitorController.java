package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.mimeter.dashboard.bo.scene.SceneTaskAppsBo;
import run.mone.mimeter.dashboard.bo.agent.AgentMonitorBo;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.BenchMonitorService;
import run.mone.mimeter.dashboard.service.impl.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "/api/bench/monitor")
@HttpApiModule(value = "BenchMonitorController", apiController = BenchMonitorController.class)
public class BenchMonitorController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private BenchMonitorService monitorService;

    /**
     * 获取某次场景压测关联的应用列表
     *
     * @param request
     * @return
     */
    @HttpApiDoc(value = "/api/bench/monitor/getAppListByReportID", apiName = "获取某次场景压测关联的应用列表", method = MiApiRequestMethod.POST, description = "获取某次场景压测关联的应用列表")
    @RequestMapping(value = "/getAppListByReportID", method = RequestMethod.POST)
    public Result<SceneTaskAppsBo> getAppListByReportID(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "sceneID",required = true,description = "场景id",defaultValue = "111")
            Integer sceneID,
            @HttpApiDocClassDefine(value = "reportID",required = true,description = "报告id",defaultValue = "dsas134")
            String reportID,
            @HttpApiDocClassDefine(value = "realTime",required = true,description = "是否实时",defaultValue = "dsas134")
            Boolean realTime
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.getAppListByTaskFlag] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return monitorService.getAppListByReportID(sceneID,reportID,realTime);
    }

    /**
     * 获取报告下的压测机数据
     *
     * @param request
     * @return
     */
    @HttpApiDoc(value = "/api/bench/monitor/getAgentInfosByReport", apiName = "获取报告下的压测机数据", method = MiApiRequestMethod.POST, description = "获取报告下的压测机数据")
    @RequestMapping(value = "/getAgentInfosByReport", method = RequestMethod.POST)
    public Result<AgentMonitorBo> getAgentInfosByReport(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "reportID",required = true,description = "报告id",defaultValue = "dsas134")
            String reportID,
            @HttpApiDocClassDefine(value = "realTime",required = true,description = "是否实时",defaultValue = "dsas134")
            Boolean realTime
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TaskController.getAgentInfoByReport] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return monitorService.getAgentInfosByReport(reportID,realTime);
    }
}
