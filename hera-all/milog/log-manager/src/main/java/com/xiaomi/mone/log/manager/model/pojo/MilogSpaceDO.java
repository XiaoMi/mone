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

package com.xiaomi.mone.log.manager.model.pojo;

import com.xiaomi.mone.log.manager.model.BaseCommon;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * @author shanwb
 * @date 2021-06-28
 */

@Table("milog_space")
@Comment("milog项目空间表")
@Data
public class MilogSpaceDO extends BaseCommon {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;


    @Column(value = "tenant_id")
    @ColDefine(customType = "bigint")
    @Comment("租户Id")
    private Long tenantId;


    @Column(value = "space_name")
    @ColDefine(type = ColType.VARCHAR, width = 256)
    @Comment("项目空间名称")
    private String spaceName;


    @Column(value = "source")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("来源")
    private String source;

    @Column(value = "perm_dept_id")
    @ColDefine(type = ColType.VARCHAR, width = 2000)
    @Comment("可查看此space的三级部门ID")
    private String permDeptId;


    @Column(value = "create_dept_id")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @Comment("创建的三级部门ID")
    private String createDeptId;


    @Column(value = "description")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("备注说明")
    private String description;
}
