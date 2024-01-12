package run.mone.mimeter.dashboard.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scenegroup.SceneGroupDTO;
import run.mone.mimeter.dashboard.mapper.SceneGroupMapper;
import run.mone.mimeter.dashboard.mapper.SceneInfoMapper;
import run.mone.mimeter.dashboard.pojo.SceneGroup;
import run.mone.mimeter.dashboard.pojo.SceneInfo;
import run.mone.mimeter.dashboard.pojo.SceneInfoExample;
import run.mone.mimeter.dashboard.service.SceneGroupService;
import run.mone.mimeter.dashboard.service.SceneService;

import java.util.List;

@Service
public class SceneGroupServiceImpl implements SceneGroupService {

    @Autowired
    private SceneGroupMapper sceneGroupMapper;

    @Autowired
    private SceneInfoMapper sceneInfoMapper;

    @Autowired
    private SceneService sceneService;

    @Override
    public Result<Integer> newSceneGroup(SceneGroupDTO sceneGroupDTO, String opUser) {
        SceneGroup sceneGroup = new SceneGroup();
        BeanUtils.copyProperties(sceneGroupDTO,sceneGroup);
        sceneGroup.setCreator(opUser);
        sceneGroup.setCtime(System.currentTimeMillis());
        sceneGroup.setTenant(sceneGroupDTO.getTenant());
        sceneGroupMapper.insert(sceneGroup);
        return Result.success(sceneGroup.getId());
    }

    @Override
    public Result<Boolean> editSceneGroup(SceneGroupDTO sceneGroupDTO, String opUser) {
        SceneGroup sceneGroup = sceneGroupMapper.selectByPrimaryKey(sceneGroupDTO.getId());
        sceneGroup.setGroupName(sceneGroupDTO.getGroupName());
        sceneGroup.setGroupDesc(sceneGroupDTO.getGroupDesc());
        if (sceneGroup.getTenant() == null){
            sceneGroup.setTenant(sceneGroupDTO.getTenant());
        }
        sceneGroupMapper.updateByPrimaryKey(sceneGroup);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> delSceneGroup(SceneGroupDTO sceneGroupDTO, String opUser) {
        SceneInfoExample example = new SceneInfoExample();
        example.createCriteria().andSceneGroupIdEqualTo(sceneGroupDTO.getId());
        List<SceneInfo> sceneInfoList = sceneInfoMapper.selectByExample(example);
        sceneInfoList.forEach(sceneInfo -> sceneService.delScene(sceneInfo.getId(),opUser));
        sceneGroupMapper.deleteByPrimaryKey(sceneGroupDTO.getId());
        return Result.success(true);
    }

}
