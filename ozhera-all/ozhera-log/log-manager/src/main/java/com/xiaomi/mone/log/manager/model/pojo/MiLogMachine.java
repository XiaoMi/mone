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
 * @description Machine information
 * @date 2021/7/16 11:26
 */
@Table("milog_machine")
@Comment("Machine information in milog")
@Data
public class MiLogMachine {
    @Id
    @Comment("Primary key Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "ctime")
    @ColDefine(customType = "bigint")
    @Comment("Creation time")
    private Long ctime;

    @Column(value = "utime")
    @ColDefine(customType = "bigint")
    @Comment("Update time")
    private Long utime;

    @Column(value = "type")
    @ColDefine(type = ColType.INT)
    @Comment("Machine resolution type: 1: agent, 2.stream")
    private Integer type;

    @Column(value = "ip")
    @ColDefine(type = ColType.TEXT)
    @Comment("machine ip")
    private String ip;

    @Column(value = "creator")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("Creator")
    private String creator;

    @Column(value = "description")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("Remarks description")
    private String description;
}
