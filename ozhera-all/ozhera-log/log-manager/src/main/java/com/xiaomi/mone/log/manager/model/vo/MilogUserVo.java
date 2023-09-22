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
package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/9 16:44
 */
@Data
public class MilogUserVo {
    /**
     * Username, such as: zhangsan
     */
    private String user;

    /**
     * Username, such as: Zhang San
     */
    private String name;

    /**
     * User display name, such as: sa zhang Zhangsan
     */
    private String displayName;

    /**
     * Department name
     */
    private String departmentName;

    /**
     * mailbox
     */
    private String email;

    /**
     * miID
     */
    private String miID;

    /**
     * miID
     */
    private String uID;

    /**
     * avatar
     */
    private String avatar;

    /**
     *
     */
    private String zone;
    /**
     * Last level department Id
     */
    private String deptId;

    private String company;

    private boolean isAdmin;
}
