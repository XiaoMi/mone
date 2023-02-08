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

import com.xiaomi.youpin.gwdash.bo.MachineLabels;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;


/**
 * @author tsingfu
 */
@Data
@Table("machine_list")
public class Machine {

    @Id
    private long id;

    @Column
    private String name;

    @Column
    @ColDefine(width = 64)
    private String ip;

    @Column
    @ColDefine(width = 128)
    private String hostname;

    @Column("my_group")
    @ColDefine(width = 45)
    private String group;

    @Column("my_desc")
    @ColDefine(width = 1024)
    private String desc;

    @Column
    private long ctime;

    @Column
    private long utime;



    /**
     * 标签信息
     */
    @ColDefine(type = ColType.MYSQL_JSON)
    @Column("labels")
    private MachineLabels labels;

    /**
     * 预先占用的标签
     */
    @ColDefine(type = ColType.MYSQL_JSON)
    @Column("prepare_labels")
    private MachineLabels prepareLabels;


    @Column(value="version",version = true)
    private int version;
}
