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

package com.xiaomi.youpin.gateway.db;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 3/14/21
 */
@Table("script_table")
@Data
public class ScriptTable {

    @Id
    private long id;

    @Column
    private long appId;

    @Column
    private long apiId;

    @Column
    private long envId;

    @Column
    private String info;

    @Column
    private int type;

    @Column
    private int subType;

    @Column
    private String name;

    @Column
    private int columnInt0;

    @Column
    private int columnInt1;

    @Column
    private int columnInt2;

    @Column
    private int columnInt3;

    @Column
    private int columnInt4;

    @Column
    private int columnString0;

    @Column
    private int columnString1;

    @Column
    private int columnString2;

    @Column
    private int columnString3;

    @Column
    private int columnString4;

    @Column
    private long utime;

    @Column
    private long ctime;

    @Column
    private int status;

    @Column
    private int version;

    @Column
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<String, String> meta;

    @Column
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<String> list;

}
