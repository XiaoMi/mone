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
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/7/14 16:01
 */
@Table("milog_store_space_auth")
@Comment("milog store授权表,store可绑定额外的space")
@Data
public class MilogStoreSpaceAuth extends BaseCommon {
    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "store_id")
    @ColDefine(customType = "bigint")
    @Comment("store主键")
    private Long storeId;

    @Column(value = "space_id")
    @ColDefine(customType = "bigint")
    @Comment("space主键")
    private Long spaceId;

}
