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

package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author goodjava@qq.com
 */
@Data
@Table(value = "plugin_info")
public class PluginInfoBo {

    @Id
    private int id;

    @Column
    private String name;

    @Column("data_id")
    private int dataId;

    @Column
    private long ctime;

    @Column
    private long utime;

    /**
     * 1 审核中
     */
    @Column
    private int status;

    @Column
    private String creator;

    @Column(wrap = true)
    private String desc;

    @Column
    private String url;

    @Column("git_group")
    private String gitGroup;

    @Column("git_name")
    private String gitName;

    /**
     * 分组信息
     */
    @Column("group_info")
    private String groupInfo;

    @Column("project_id")
    private int projectId;

    /**
     * 申请的唯一key
     */
    @Column("flow_key")
    @ColDefine(width = 100)
    private String flowKey;

    @Column("git_domain")
    private String gitDomain;

    private String dataVersion;

    @Column
    private String tenant;

}
