package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scene.CreateSceneDTO;
import run.mone.mimeter.dashboard.bo.scene.EditSceneDTO;
import run.mone.mimeter.dashboard.bo.scenegroup.SceneGroupDTO;

public interface SceneGroupService {
    Result<Integer> newSceneGroup(SceneGroupDTO sceneGroupDTO, String opUser);

    Result<Boolean> delSceneGroup(SceneGroupDTO sceneGroupDTO, String opUser);

    Result<Boolean> editSceneGroup(SceneGroupDTO sceneGroupDTO, String opUser);
}
