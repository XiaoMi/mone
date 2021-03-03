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

package com.xiaomi.youpin.gwdash.dao.model;

import com.xiaomi.youpin.mischedule.api.service.bo.HealthResult;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author tsingfu
 */
@Data
@Table("project_env")
public class ProjectEnv {

    @Id
    private long id;

    @Column
    private String name;

    @Column("project_id")
    private long projectId;

    @Column("project_name")
    private String projectName;

    @Column("my_group")
    private String group;

    @Column("deploy_type")
    private int deployType;

    /**
     * 部署权限
     * 参考DeploymentAuthorityEnum
     */
    @Column
    private int authority;

    @Column
    private String branch;

    @Column
    private String profile;

    /**
     * 记录当前环境上线成功pipeline
     */
    @Column("pipeline_id")
    private long pipelineId;

    @Column
    private int status;

    @Column
    private long ctime;

    @Column
    private long utime;


    @Column("last_auto_scale_time")
    private long lastAutoScaleTime;

    /**
     * 健康监测的任务id
     * 没有的话是0
     */
    @Column("health_check_task_id")
    private int healthCheckTaskId;


    @ColDefine(type = ColType.MYSQL_JSON)
    @Column("health_check_result")
    private HealthResult healthResult;
}
