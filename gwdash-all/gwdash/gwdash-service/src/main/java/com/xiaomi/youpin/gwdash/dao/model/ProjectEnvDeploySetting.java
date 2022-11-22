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

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * @author tsingfu
 */
@Data
@Table("project_env_deploy_setting")
public class ProjectEnvDeploySetting {
    @Id
    private long id;

    @Column("env_id")
    private long envId;

    @Column
    @ColDefine(width = 128)
    private String path;

    @Column("heapSize")
    private long heapSize;

    @Column("docker_port")
    private int dockerPort;

    @Column("cpu")
    @ColDefine(width = 45)
    private String cpu;

    @Column("blkio_weight")
    private int blkioWeight;

    @Column("log_path")
    @ColDefine(width = 128)
    private String logPath;

    @Column
    private long memory;

    @Column
    private String volume;

    @Column("jvm_params")
    @ColDefine(width = 2048)
    private String jvmParams = "";

    @Column("dockerfile_path")
    @ColDefine(width = 2048)
    private String dockerfilePath = "";

    @Column("labels")
    @ColDefine(width = 200)
    private String labels;

    //最小实列数
    @Column
    private long replicate;

    //最大实列数
    @Column("max_replicate")
    private long maxReplicate;

    /**
     * http://127.0.0.1:80/health
     * dubbo://127.0.0.1/com.xiaomi.Test/group/health
     */
    @Column("health_check_url")
    private String healthCheckUrl;
}
