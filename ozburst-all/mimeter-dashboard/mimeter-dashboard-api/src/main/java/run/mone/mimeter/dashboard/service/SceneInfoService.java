package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scene.SceneDTO;

public interface SceneInfoService {
    Result<SceneDTO> getSceneByID(Integer sceneID);

    void updateSceneStatus(Integer sceneId,Integer sceneStatus);

    Result<Boolean> updatemimeterTaskStatus(String report,Integer status);

    Result<Boolean> updateSceneTenant(Integer sceneId,String tenant);

    Result<Boolean> tmpUpdateLogRate(int sceneId,int logRate);

    Result<Boolean> processLossTask();
}
