package com.xiaomi.youpin.teambition.bo;

import lombok.Data;

@Data
public class TaskInfo {
    private String operatorId;
    private String taskId;
    private String projectId;
    private String templateId;//任务类型 ID
    private String tasklistId;//任务列表 ID
    private String taskgroupId;//任务分组 ID
    private String content;
    private String statusId;//工作流状态 ID
    private String startDate;
    private String dueDate;
    private String note;
    private int isDone;
    private String parentTaskId;
    private String accomplishDate;//完成时间
    private String executorId;//执行者的用户 ID
    private String executorName;//执行者的用户 name
    private String creatorId;//创建人的用户 ID
    private String creatorName;//创建人的用户 name
    private String priority;//优先级：0：普通（默认值) 1：紧急 2：非常紧急

}
