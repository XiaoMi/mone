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
 * @author shanwb
 * @date 2021-06-28
 */

@Table("milog_space")
@Comment("Milog project space table")
@Data
public class MilogSpaceDO extends BaseCommon {
    @Id
    @Comment("Primary key Id")
    @ColDefine(customType = "bigint")
    private Long id;


    @Column(value = "tenant_id")
    @ColDefine(customType = "bigint")
    @Comment("Tenant ID")
    private Long tenantId;


    @Column(value = "space_name")
    @ColDefine(type = ColType.VARCHAR, width = 256)
    @Comment("Project space name")
    private String spaceName;


    @Column(value = "source")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("source")
    private String source;

    @Column(value = "perm_dept_id")
    @ColDefine(type = ColType.VARCHAR, width = 2000)
    @Comment("You can view the third-level department ID of this space")
    private String permDeptId;


    @Column(value = "create_dept_id")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @Comment("The created three-level department ID")
    private String createDeptId;


    @Column(value = "description")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("Remarks description")
    private String description;
}
