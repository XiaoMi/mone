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

import com.xiaomi.youpin.gwdash.bo.DeployInfo;
import com.xiaomi.youpin.gwdash.bo.DeployResult;
import com.xiaomi.youpin.gwdash.bo.DeploySetting;
import com.xiaomi.youpin.gwdash.bo.ReviewBo;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author tsingfu
 */
@Data
@Table("project_pipeline")
public class ProjectPipeline {

    @Id
    private long id;

    @Column("project_id")
    private long projectId;

    @Column("env_id")
    private long envId;

    @Column("compilation_id")
    private long compilationId;

    @Column("code_check_id")
    private long codeCheckId;

    @Column("build_type")
    private String buildType;

    /**
     * 部署人
     */
    @Column("username")
    private String username;

    /**
     * 记录要回滚的pipelineId
     */
    @Column("rollback_id")
    private long rollbackId;

    @Column
    private int status;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column(version = true)
    private int version;

    @Column("error_message")
    @ColDefine(type = ColType.MYSQL_JSON)
    private ErrorMessage errorMessage;

    @ColDefine(type = ColType.MYSQL_JSON)
    @Column("deploy_setting")
    private DeploySetting deploySetting;

    @ColDefine(type = ColType.MYSQL_JSON)
    @Column("deploy_result")
    private DeployResult deployResult;

    @ColDefine(type = ColType.MYSQL_JSON)
    @Column("deploy_info")
    private DeployInfo deployInfo;

    @One(field = "codeCheckId", target = ProjectCodeCheckRecord.class)
    private ProjectCodeCheckRecord projectCodeCheckRecord;

    @One(field = "compilationId", target = ProjectCompileRecord.class)
    private ProjectCompileRecord projectCompileRecord;

    private ReviewBo review;
}
