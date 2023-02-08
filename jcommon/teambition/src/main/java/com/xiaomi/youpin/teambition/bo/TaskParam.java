package com.xiaomi.youpin.teambition.bo;

import lombok.Data;

/**
 * @author wmin
 * @date 2021/9/18
 */
@Data
public class TaskParam {
    private String taskId;
    private String parentTaskId;
    private String userId;
    private String organizationId;
    /**
     * 查询条件，取值为：
     * executor：查询用户执行的任务（默认值）
     * creator：查询用户创建的任务
     * participant：查询用户参与的任务
     */
    private String condition;
    private String isDone;
    private String projectId;
    private int pageSize;
    private String pageToken;
}
