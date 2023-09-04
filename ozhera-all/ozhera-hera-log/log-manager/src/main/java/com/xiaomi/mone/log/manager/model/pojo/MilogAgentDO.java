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

@Table("milog_log_agent")
@Comment("milog Agent")
@Data
public class MilogAgentDO {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "ip")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("agent ip")
    private String ip;

    @Column(value = "computer_room_id")
    @ColDefine(customType = "bigint")
    @Comment("机房ID")
    private Long computerRoomId;

    @Column(value = "container")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("容器")
    private String container;

    @Column(value = "state")
    @ColDefine(type = ColType.INT)
    @Comment("agent状态")
    private Integer state;

    @Column(value = "version")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("agent版本")
    private String version;

    @Column(value = "updateTime")
    @ColDefine(customType = "bigint")
    @Comment("版本更新时间")
    private Long updateTime;

    @Column(value = "ctime")
    @ColDefine(customType = "bigint")
    @Comment("创建时间")
    private Long ctime;

    @Column(value = "utime")
    @ColDefine(customType = "bigint")
    @Comment("更新时间")
    private Long utime;

}
