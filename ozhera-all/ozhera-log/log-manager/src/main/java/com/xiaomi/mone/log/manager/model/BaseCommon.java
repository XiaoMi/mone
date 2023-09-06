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
package com.xiaomi.mone.log.manager.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/17 17:01
 */
@Data
public class BaseCommon {

    @Column(value = "ctime")
    @ColDefine(customType = "bigint")
    @Comment("create time")
    private Long ctime;


    @Column(value = "utime")
    @ColDefine(customType = "bigint")
    @Comment("update time")
    private Long utime;


    @Column(value = "creator")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @Comment("creator")
    private String creator;


    @Column(value = "updater")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @Comment("updater")
    private String updater;
}
