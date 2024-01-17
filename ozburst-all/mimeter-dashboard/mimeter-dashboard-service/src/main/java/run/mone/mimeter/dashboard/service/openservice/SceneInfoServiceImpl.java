package run.mone.mimeter.dashboard.service.openservice;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scene.SceneDTO;
import run.mone.mimeter.dashboard.common.TaskStatus;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.mapper.MibenchTaskMapper;
import run.mone.mimeter.dashboard.mapper.SceneInfoMapper;
import run.mone.mimeter.dashboard.pojo.MibenchTask;
import run.mone.mimeter.dashboard.pojo.MibenchTaskExample;
import run.mone.mimeter.dashboard.pojo.SceneInfo;
import run.mone.mimeter.dashboard.service.SceneInfoService;
import run.mone.mimeter.dashboard.service.SceneService;

import java.util.List;
import java.util.stream.Collectors;

@DubboService(group = "${dubbo.group}",version = "${dubbo.version}")
@Slf4j
public class SceneInfoServiceImpl implements SceneInfoService {

    @Autowired
    private SceneService sceneService;

    @Autowired
    private SceneInfoMapper sceneInfoMapper;

    @Autowired
    private MibenchTaskMapper mibenchTaskMapper;

    /**
     * 15s后未结束认为状态已丢失
     */
    private static final long MAX_DELAY_TIME = 15;

    @Override
    public Result<SceneDTO> getSceneByID(Integer sceneID) {
        return sceneService.getSceneByID(sceneID,true);
    }

    @Override
    public void updateSceneStatus(Integer sceneId,Integer sceneStatus) {
        if (sceneId == null || sceneStatus == null){
            return;
        }
        try {
            SceneInfo sceneInfo = sceneInfoMapper.selectByPrimaryKey(sceneId);
            sceneInfo.setSceneStatus(sceneStatus);
            sceneInfoMapper.updateByPrimaryKey(sceneInfo);
        } catch (Exception e) {
            log.error("update scene status error:{}",e.getMessage());
        }
    }

    @Override
    public Result<Boolean> updatemimeterTaskStatus(String report, Integer status) {
        MibenchTaskExample example = new MibenchTaskExample();
        example.createCriteria().andReportIdEqualTo(report);
        List<MibenchTask> MibenchTasks = mibenchTaskMapper.selectByExample(example);
        MibenchTasks.forEach(MibenchTask -> {
            MibenchTask.setState(status);
            mibenchTaskMapper.updateByPrimaryKey(MibenchTask);
        });

        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateSceneTenant(Integer sceneId, String tenant) {
        SceneInfo sceneInfo = sceneInfoMapper.selectByPrimaryKey(sceneId);
        sceneInfo.setTenant(tenant);
        sceneInfoMapper.updateByPrimaryKey(sceneInfo);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> tmpUpdateLogRate(int sceneId, int logRate) {
        SceneInfo sceneInfo = sceneInfoMapper.selectByPrimaryKey(sceneId);
        sceneInfo.setLogRate(logRate);
        sceneInfoMapper.updateByPrimaryKey(sceneInfo);
        return Result.success(true);
    }

    /**
     * 定时任务，处理丢失状态的任务
     * @return
     */
    @Override
    public Result<Boolean> processLossTask() {
        try {
            MibenchTaskExample example = new MibenchTaskExample();
            example.createCriteria().andStateEqualTo(TaskStatus.Running.code);
            List<MibenchTask> runningTasks = mibenchTaskMapper.selectByExample(example);
            if (runningTasks == null || runningTasks.size() == 0){
                return Result.success(true);
            }
            List<MibenchTask> toDoTaskList = runningTasks.stream().filter(runningTask -> (System.currentTimeMillis() - runningTask.getCtime()) >= (runningTask.getTime() + MAX_DELAY_TIME)).collect(Collectors.toList());
            if (toDoTaskList.size() == 0){
                return Result.success(true);
            }
            toDoTaskList.forEach(toDoTask ->{
                toDoTask.setState(TaskStatus.Success.code);
                mibenchTaskMapper.updateByPrimaryKey(toDoTask);
            });
        } catch (Exception e) {
            log.error("SceneInfoServiceImpl processLossTask failed,cause by:{}",e.getMessage());
            return Result.fail(CommonError.UnknownError);
        }
        return Result.success(true);
    }
}
