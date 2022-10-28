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


import com.xiaomi.youpin.gwdash.common.ReviewStatusEnum;
import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Data
@Table("review")
public class Review {

    @Id
    private int id;

    @Column("project_id")
    private long projectId;


    @Column("project_name")
    private String projectName;

    @Column("commit_id")
    private String commitId;

    @Column
    private String url;

    @Column
    private String submitter;

    @Column
    private String reviewer;

    @Column
    private String operator;

    @Column("operate_time")
    private long operateTime;

    /**
     * @see ReviewStatusEnum
     */
    @Column
    private int status;

    @Column
    private String remarks;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column(version = true)
    private int version;

    /**
     * 用于回调miline流水线
     */
    @Column("pipeline_record_id")
    private long pipelineRecordId;

}
