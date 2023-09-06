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
@Comment("milog Store authorization form, store can bind additional space")
@Data
public class MilogStoreSpaceAuth extends BaseCommon {
    @Id
    @Comment("Primary key Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "store_id")
    @ColDefine(customType = "bigint")
    @Comment("Store primary key")
    private Long storeId;

    @Column(value = "space_id")
    @ColDefine(customType = "bigint")
    @Comment("Space primary key")
    private Long spaceId;

}
