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
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Data
@Table("sonarqube_config")
public class SonarQubeConfig {

    @Id
    private long id;

    @Column("project_id")
    private long projectId;

    @Column
    private String branch;

    @Column("project_key")
    private String projectKey;

    @Column
    private String profile;

    /**
     * @see com.xiaomi.youpin.gwdash.common.SonarQubeStatusEnum
     */
    @Column
    private Integer status;

    /**
     * mischedule task id
     */
    @Column("task_id")
    private long taskId;

    @Column("build_path")
    private String buildPath;

    @Column
    private long ctime;

    @Column
    private long utime;
}
