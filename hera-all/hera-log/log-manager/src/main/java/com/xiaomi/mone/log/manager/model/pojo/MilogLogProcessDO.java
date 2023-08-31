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

@Table("milog_log_process")
@Comment("日志收集进度")
@Data
public class MilogLogProcessDO {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "tailId")
    @ColDefine(customType = "bigint")
    @Comment("tailId")
    private Long tailId;

    @Column(value = "agent_id")
    @ColDefine(customType = "bigint")
    @Comment("agentId")
    private Long agentId;

    @Column(value = "ip")
    @ColDefine(type = ColType.VARCHAR, width = 256)
    @Comment("ip")
    private String ip;

    @Column(value = "file_row_number")
    @ColDefine(type = ColType.INT)
    @Comment("日志文件行号")
    private Integer fileRowNumber;

    @Column(value = "pointer")
    @ColDefine(type = ColType.INT)
    private Long pointer;

    @Column(value = "collect_time")
    @ColDefine(customType = "bigint")
    @Comment("日志收集时间")
    private Long collectTime;

    @Column(value = "ctime")
    @ColDefine(customType = "bigint")
    @Comment("创建时间")
    private Long ctime;

    @Column(value = "utime")
    @ColDefine(customType = "bigint")
    @Comment("更新时间")
    private Long utime;

}
