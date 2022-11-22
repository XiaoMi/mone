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
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author goodjava@qq.com
 * 审批
 */
@Data
@Table("approval")
public class Approval {

    @Id
    private int id;

    @Column(wrap = true)
    private String key;

    /**
     * 0 还没有创建 1 待审批 2 通过 3 驳回
     */
    @Column
    private int status;

    @Column
    private int version;

    @Column
    private long utime;

    @Column
    private long ctime;

    @Column
    private int projectId;

    /**
     * 申请者
     */
    @Column
    private int applicantId;

//    /**
//     * 审批人id
//     */
//    @Column
//    private int auditorId;


    /**
     * 理由
     */
    @Column
    @ColDefine(width = 200)
    private String reason;

    /**
     * ApprovalType
     */
    @Column
    private int type;


    /**
     * 业务id
     * 如果是filter申请,则
     */
    @Column("biz_id")
    private int bizId;

    /**
     * 申请人名字
     */
    @Column("applicant_name")
    private String applicantName;

    /**
     * commitId
     */
    // todo: 需要修改
    @Column("commit_id")
    private String content;

    private String branch;

    private String commitId;

    private int page = 1;

    private int pageSize = 20;

//    /**
//     * 环境id
//     */
//    @Column("env_id")
//    private long envId;

}
