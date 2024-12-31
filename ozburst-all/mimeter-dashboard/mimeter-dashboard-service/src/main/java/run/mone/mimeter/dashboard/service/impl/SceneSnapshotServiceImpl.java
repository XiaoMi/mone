package run.mone.mimeter.dashboard.service.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.SceneNameDTO;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scene.SceneSnapshotBo;
import run.mone.mimeter.dashboard.common.util.Util;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.mapper.SceneSnapshotMapper;
import run.mone.mimeter.dashboard.pojo.SceneSnapshot;
import run.mone.mimeter.dashboard.pojo.SceneSnapshotExample;
import run.mone.mimeter.dashboard.service.SceneSnapshotService;
import run.mone.mimeter.dashboard.common.util.Utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static run.mone.mimeter.dashboard.bo.snapshot.SnapshotTypeEnum.SCENE_SNAPSHOT;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/23
 */
@Slf4j
@Service
public class SceneSnapshotServiceImpl implements SceneSnapshotService {

    @Autowired
    private SceneSnapshotMapper sceneSnapshotMapper;

    private static final Gson gson = Util.getGson();

    @Override
    public Result<SceneSnapshotBo> getSceneSnapshotById(String snapshotId) {
        SceneSnapshotExample example = new SceneSnapshotExample();
        SceneSnapshotExample.Criteria criteria = example.createCriteria();
        criteria.andSnapshotIdEqualTo(snapshotId);
        return this.getSceneSnapshotByExample(example, true);
    }

    private Result<SceneSnapshotBo> getSceneSnapshotByExample(SceneSnapshotExample example, boolean withBlob) {
        List<SceneSnapshot> list = withBlob ? this.sceneSnapshotMapper.selectByExampleWithBLOBs(example) :
                this.sceneSnapshotMapper.selectByExample(example);

        if (list.isEmpty()) {
            return Result.success(null);
        }
        SceneSnapshotBo bo = new SceneSnapshotBo();
        BeanUtils.copyProperties(list.get(0), bo);
        return Result.success(bo);
    }

    @Override
    public Result<SceneSnapshotBo> getSceneSnapshotByScene(Long sceneId) {
        return this.doGetSceneSnapshotByScene(sceneId, SCENE_SNAPSHOT.typeCode, false);
    }

    private Result<SceneSnapshotBo> doGetSceneSnapshotByScene(Long sceneId, int type, boolean withBlob) {
        SceneSnapshotExample example = new SceneSnapshotExample();
        SceneSnapshotExample.Criteria criteria = example.createCriteria();
        criteria.andSceneIdEqualTo(sceneId);
//        criteria.andTypeEqualTo(type);
        example.setOrderByClause("id desc");
        example.setLimit(1);
        return this.getSceneSnapshotByExample(example, withBlob);
    }

    @Override
    public Result<SceneSnapshotBo> getSceneSnapshotDetailByScene(Long sceneId) {
        return this.doGetSceneSnapshotByScene(sceneId, SCENE_SNAPSHOT.typeCode, true);
    }

    @Override
    public Result<String> createSnapshot(SceneSnapshotBo bo) {
        String logPrefix = "[SceneSnapshotService]";
        checkArgument(bo != null && bo.checkCreate(), logPrefix + "createSnapshot invalid input: " + new Gson().toJson(bo));
        Result<SceneSnapshotBo> oldResult = getSceneSnapshotByScene(bo.getSceneId());
        int version = 1;

        if (oldResult.getData() != null) {
            version += Optional.ofNullable(oldResult.getData().getVersion()).orElse(0);
        }

        String snapshotId = Utility.saltVersionedId(bo.getSceneId(), version);
        bo.setSnapshotId(snapshotId);
        bo.setMd5(Utility.generateSha256(bo.getScene()));
        bo.setVersion(version);

        SceneSnapshot po = new SceneSnapshot();
        BeanUtils.copyProperties(bo, po);

        if (this.sceneSnapshotMapper.insertSelective(po) > 0) {
            return Result.success(snapshotId);
        }
        return Result.fail(CommonError.UnknownError);
    }

     public Map<Integer, String> sceneNameMapFromIds(List<String> snapshotIds) {
        SceneSnapshotExample example = new SceneSnapshotExample();
        example.createCriteria().andSnapshotIdIn(snapshotIds);
        List<SceneSnapshot> snapshotList = this.sceneSnapshotMapper.selectByExampleWithBLOBs(example);
        Map<Integer, String> sceneNameMap = new HashMap<>();

        if (snapshotList != null) {
            snapshotList.forEach(snapshot -> {
                SceneNameDTO nameDTO = gson.fromJson(snapshot.getScene(), SceneNameDTO.class);
                sceneNameMap.putIfAbsent(Math.toIntExact(snapshot.getSceneId()), nameDTO.getName());
            });
        }
        return sceneNameMap;
    }
}
