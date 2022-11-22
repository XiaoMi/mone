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

import com.xiaomi.youpin.gwdash.dao.model.ProjectCompileRecord;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * @author dp
 */
@Data
@Table(value = "filter_info")
public class FilterInfoBo {

    @Id
    private long id;

    @Column
    private String name;

    @Column
    private String cname;

    @Column
    private String author;

    @Column(wrap = true)
    private String desc;

    @Column("git_address")
    private String gitAddress;

    @Column("git_group")
    private String gitGroup;

    @Column("git_name")
    private String gitName;

    @Column("commit_id")
    private String commitId;

    @Column("compile_id")
    private long compileId;

    @Column
    private String params;

    @Column
    private byte[] data;

    @Column
    private int status;

    @Column("online_status")
    private int onlineStatus;

    @Column("is_system")
    private int isSystem;

    @Column
    private String version;

    @Column
    private String creator;

    @Column
    private long ctime;

    @Column
    private long utime;

    /**
     * 插件所属分组
     */
    @Column
    private String groups;

    /**
     * 隶属的项目
     */
    @Column("project_id")
    private long projectId;

    @One(target = ProjectCompileRecord.class, key = "id", field = "compileId")
    private ProjectCompileRecord projectCompileRecord;

    @Many(target = UserRateBo.class, field = "theRateId")
    private List<UserRateBo> userRateBos;
}
