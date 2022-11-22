package com.xiaomi.miapi.service.impl;

import com.xiaomi.miapi.common.pojo.Api;
import com.xiaomi.miapi.common.pojo.ProjectOperationLog;
import com.xiaomi.miapi.mapper.ProjectOperationLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecordService {

    @Autowired
    ProjectOperationLogMapper projectOperationLogMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordService.class);

    public void doRecord(Api api, String historyJson, String hisDesc, String projectLogDesc, Integer opType){
        if (api == null){
            return;
        }
        ProjectOperationLog projectOperationLog = new ProjectOperationLog();
        projectOperationLog.setOpProjectID(api.getProjectID());
        projectOperationLog.setOpDesc(projectLogDesc);
        projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_API);
        if (api.getApiID() != null){
            projectOperationLog.setOpTargetID(api.getApiID());
        }else {
            projectOperationLog.setOpTargetID(0);
        }
        projectOperationLog.setOpTime(api.getApiUpdateTime());
        projectOperationLog.setOpType(opType);
        projectOperationLog.setOpUsername(api.getUpdateUsername());
        if (api.getUpdateUsername() == null){
            projectOperationLog.setOpUsername("system user");
        }
        if (projectOperationLogMapper.addProjectOperationLog(projectOperationLog) < 1){
            LOGGER.error("添加项目操作记录失败");
        }
    }
}
