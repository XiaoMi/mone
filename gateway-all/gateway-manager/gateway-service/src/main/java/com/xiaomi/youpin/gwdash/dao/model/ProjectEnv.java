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

//import com.xiaomi.youpin.mischedule.api.service.bo.HealthResult;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @author tsingfu
 */
@Data
@Table("project_env")
public class ProjectEnv implements Serializable {

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

    @Column("test_service")
    private String testService;

    @Column
    private String tenement;

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

    /**
     * 刷新keycenter deploy token的任务id
     */
    @Column("kc_refresh_token_task_id")
    private int kcRefreshTokenTaskId;

    /**
     * 进程监控的任务id
     * 没有的话是0
     */
    @Column("process_monitor_task_id")
    private int processMonitorTaskId;


//    @ColDefine(type = ColType.MYSQL_JSON)
//    @Column("health_check_result")
//    private HealthResult healthResult;


    @Column("web_host")
    private String webHost;

    @Column("staging_tag")
    private String stagingTag;

    @Column("many_staging")
    private int manyStaging;

    /**
     * 是否开启分支集成
     */
    @Column("branch_integration")
    private boolean branchIntegration;

    /**
     * 是否设置成自动部署
     */
    @Column("auto_deploy")
    private boolean autoDeploy;

}
