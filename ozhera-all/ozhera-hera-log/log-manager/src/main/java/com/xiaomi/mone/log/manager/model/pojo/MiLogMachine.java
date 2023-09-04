/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.model.pojo;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author wtt
 * @version 1.0
 * @description 机器信息
 * @date 2021/7/16 11:26
 */
@Table("milog_machine")
@Comment("milog中机器信息")
@Data
public class MiLogMachine {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "ctime")
    @ColDefine(customType = "bigint")
    @Comment("创建时间")
    private Long ctime;

    @Column(value = "utime")
    @ColDefine(customType = "bigint")
    @Comment("更新时间")
    private Long utime;

    @Column(value = "type")
    @ColDefine(type = ColType.INT)
    @Comment("机器解析类型：1:agent，2.stream")
    private Integer type;

    @Column(value = "ip")
    @ColDefine(type = ColType.TEXT)
    @Comment("机器ip")
    private String ip;

    @Column(value = "creator")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("创建者")
    private String creator;

    @Column(value = "description")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("备注说明")
    private String description;
}
