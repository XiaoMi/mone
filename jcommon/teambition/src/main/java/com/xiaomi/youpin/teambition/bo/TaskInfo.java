/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
