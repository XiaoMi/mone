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
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author tsingfu
 */
@Data
@Table("project_deploy_record")
public class ProjectDeployRecord {

    @Id
    private long id;

    @Column("pipeline_id")
    private long pipelineId;

    @Column("env_id")
    private long envId;

    @Column
    private int step;

    @Column
    private int status;

    /**
     * 仅兼容使用，请不要使用
     */
    @Deprecated
    @ColDefine(type = ColType.MYSQL_JSON)
    @Column("deploy_info")
    private DeployInfo deployInfo;

    @Column
    private long time;

    @Column
    private long ctime;

    @Column
    private long utime;
}
