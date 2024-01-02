package run.mone.mimeter.dashboard.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.operationlog.GetOperationLogListReq;
import run.mone.mimeter.dashboard.bo.operationlog.OperationLogDto;
import run.mone.mimeter.dashboard.bo.operationlog.OperationLogList;
import run.mone.mimeter.dashboard.bo.operationlog.PerOperation;
import run.mone.mimeter.dashboard.mapper.OperationLogMapper;
import run.mone.mimeter.dashboard.pojo.OperationLog;
import run.mone.mimeter.dashboard.pojo.OperationLogExample;
import run.mone.mimeter.dashboard.service.OperationLogService;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static run.mone.mimeter.dashboard.bo.common.Constants.DEFAULT_PAGE_SIZE;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    OperationLogMapper operationLogMapper;

    private static Gson gson = new Gson();

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 30,TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(30));


    @Override
    public void newOperationLog(OperationLogDto param) {
        executor.execute(() -> {
            OperationLog operationLog = toOperationLog(param);
            operationLogMapper.insert(operationLog);});
    }

    @Override
    public Result<OperationLogList> getOperationLogList(GetOperationLogListReq req) {

        if (req.getPage() <= 0) {
            req.setPage(1);
        }
        if (req.getPageSize() <= 0) {
            req.setPageSize(DEFAULT_PAGE_SIZE);
        }
        int offset = (req.getPage() - 1) * req.getPageSize();

        OperationLogExample totalOperationLogExample = new OperationLogExample();
        OperationLogExample.Criteria totalCriteria = totalOperationLogExample.createCriteria();
        OperationLogExample operationLogExample = new OperationLogExample();
        OperationLogExample.Criteria criteria = operationLogExample.createCriteria();

        if (req.getEndTime() != 0) {
            totalCriteria.andCreateTimeLessThanOrEqualTo(req.getEndTime());
            criteria.andCreateTimeLessThanOrEqualTo(req.getEndTime());
        }
        if (req.getStartTime() != 0) {
            totalCriteria.andCreateTimeGreaterThanOrEqualTo(req.getStartTime());
            criteria.andCreateTimeGreaterThanOrEqualTo(req.getStartTime());
        }
        if (req.getSceneId() != 0) {
            totalCriteria.andSceneIdEqualTo(req.getSceneId());
            criteria.andSceneIdEqualTo(req.getSceneId());
        }

        operationLogExample.setOrderByClause("id desc limit " + req.getPageSize() + " offset " + offset);

        OperationLogList operationLogList = new OperationLogList();
        operationLogList.setPage(req.getPage());
        operationLogList.setPageSize(req.getPageSize());
        List<OperationLog> operationLogs = operationLogMapper.selectByExampleWithBLOBs(operationLogExample);
        if (operationLogs == null || operationLogs.size() == 0) {
            return Result.success(operationLogList);
        }
        List<OperationLogDto> operationLogDtos = operationLogs.stream().map(it -> {
            return toOperationLogDto(it);
        }).collect(Collectors.toList());
        operationLogList.setList(operationLogDtos);
        operationLogList.setTotal(operationLogMapper.countByExample(totalOperationLogExample));

        return Result.success(operationLogList);
    }


    private OperationLog toOperationLog(OperationLogDto param) {
        OperationLog operationLog = new OperationLog();
        BeanUtils.copyProperties(param, operationLog);
        if (param.getSupportOperation() != null && param.getSupportOperation().size() > 0) {
            operationLog.setSupportOperation(gson.toJson(param.getSupportOperation()));
        }
        long now = System.currentTimeMillis();
        if (operationLog.getCreateTime() == null || operationLog.getCreateTime() == 0) {
            operationLog.setCreateTime(now);
        }
        operationLog.setUpdateTime(now);
        return operationLog;
    }

    private OperationLogDto toOperationLogDto(OperationLog operationLog) {
        OperationLogDto operationLogDto = new OperationLogDto();
        BeanUtils.copyProperties(operationLog, operationLogDto);
        if (StringUtils.isNotEmpty(operationLog.getSupportOperation())) {
            operationLogDto.setSupportOperation(gson.fromJson(operationLog.getSupportOperation(), new TypeToken<List<PerOperation>>() {
            }.getType()));
        }
        return operationLogDto;
    }


}
