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

import com.xiaomi.mone.log.manager.model.BaseCommon;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author zhangping17
 * @date 2021-10-15 Region,AZ实体
 */
@Table("milog_region_zone")
@Comment("milog区域可用区")
@Data
public class MilogRegionAvailableZoneDO extends BaseCommon {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "region_name_en")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("region英文名")
    private String regionNameEN;

    @Column(value = "region_name_cn")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("region中文名")
    private String regionNameCN;

    @Column(value = "zone_name_en")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("zone英文名")
    private String zoneNameEN;

    @Column(value = "zone_name_cn")
    @ColDefine(type = ColType.VARCHAR)
    @Comment("zone中文名")
    private String zoneNameCN;
}
