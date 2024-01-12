package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.dataset.SceneParamData;
import run.mone.mimeter.dashboard.bo.scene.*;
import run.mone.mimeter.dashboard.bo.scenegroup.GetSceneGroupListReq;
import run.mone.mimeter.dashboard.bo.scenegroup.SceneGroupList;
import run.mone.mimeter.dashboard.pojo.SceneInfo;

import java.util.List;

public interface SceneService {
    Result<SceneDTO> newScene(CreateSceneDTO createSceneReq, String opUser);

    Result<Boolean> delScene(Integer sceneID, String opUser);

    Result<Boolean> editScene(EditSceneDTO editSceneReq, String opUser);

    Result<SceneDTO> getSceneByID(Integer sceneID,boolean engine);

    Result<SceneList> getSceneList(GetSceneListReq req);

    Result<List<SceneDTO>> getSceneListByIds(GetSceneListByIdsReq req);

    Result<SceneGroupList> getSceneListByGroup(GetSceneGroupListReq req);

    Result<List<SceneInfo>> getSceneInfoByIds(List<Integer> sceneIds);

    Result<SceneParamData> getSceneParamData(Integer sceneId);

    Result<SceneList> getSceneListByKeyword(GetSceneListReq req);

    List<SerialLinkDTO> getSerialLinksByIds(List<Integer> serialIds);
}
