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

/**
 * @author goodjava@qq.com
 * <p>
 * 配额
 */
@Table("quota")
@Data
public class Quota {

    @Id
    private int id;

    /**
     * 资源id
     */
    @Column("resource_id")
    private int resourceId;

    @Column("biz_id")
    private long bizId;

    @Column
    private int cpu;

    @Column
    private long mem;

    @Column("ports")
    @ColDefine(type = ColType.MYSQL_JSON)
    private List<Integer> ports;

    @Column
    private String ip;

    /**
     * 项目名称
     */
    private String projectName;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column
    private int version;

    @Column
    private int status;

}
