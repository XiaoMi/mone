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

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * 项目
 */
@Data
@Table("project")
public class Project implements Serializable {

    @Id
    private long id;

    @Column
    @ColDefine(width = 100)
    private String name;

    @Column(wrap = true)
    @ColDefine(width = 100)
    private String desc;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column
    private int status;

    @Column
    @ColDefine(width = 200)
    private String gitAddress;

    @Column("git_group")
    @ColDefine(width = 64)
    private String gitGroup;

    @Column("git_name")
    @ColDefine(width = 64)
    private String gitName;

    @ColDefine(type = ColType.MYSQL_JSON)
    @Column("project_gen")
    private ProjectGen projectGen;

    @Column
    private int version;

    @Column("deploy_limit")
    private int deployLimit;

    private int page = 1;

    private int pageSize = 20;

    private boolean showAll;

    private String search;

    /**
     * for dependency
     */
    private List<Dependency> dependency;

    private String domain;

    @Column("iam_tree_id")
    private Long iamTreeId;

}
