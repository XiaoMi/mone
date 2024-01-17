package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.scene.SceneTaskAppsBo;
import run.mone.mimeter.dashboard.bo.agent.AgentMonitorBo;
import run.mone.mimeter.dashboard.bo.common.Result;

public interface BenchMonitorService {
    Result<SceneTaskAppsBo> getAppListByReportID(Integer sceneId,String reportId,Boolean realTime);

    Result<AgentMonitorBo> getAgentInfosByReport(String reportId, Boolean realTime);

}
