package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scene.SceneSnapshotBo;

import java.util.List;
import java.util.Map;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/23
 */
public interface SceneSnapshotService {

    Result<SceneSnapshotBo> getSceneSnapshotById(String snapshotId);

    /**
     * only return meta data; does not contain scene data
     */
    Result<SceneSnapshotBo> getSceneSnapshotByScene(Long sceneId);

    /**
     * return scene data as well
     */
    Result<SceneSnapshotBo> getSceneSnapshotDetailByScene(Long sceneId);

    Result<String> createSnapshot(SceneSnapshotBo bo);

    Map<Integer, String> sceneNameMapFromIds(List<String> snapshotIds);
}
