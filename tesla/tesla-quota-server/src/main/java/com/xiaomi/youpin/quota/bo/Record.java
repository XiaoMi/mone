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

package com.xiaomi.youpin.quota.bo;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 
 * <p>
 * record
 */

@Table("record")
@Data
public class Record {

    @Id
    private int id;

    @Column
    private  String ip;

    @Column("biz_id")
    private long bizId;

    @Column("project_before")
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<ResourceBo> projectBefore;

    @Column("project_after")
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<ResourceBo> projectAfter;


    @Column("resource_before")
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<Long, BizResource> resourceBefore;

    @Column("resource_after")
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<Long, BizResource> resourceAfter;


    //扩容,drift, offline, etc...
    @Column("type_op")
    private String type;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column
    private int version;

    @Column
    private int status;

    //操作的人
    @Column
    private String operator;
}
