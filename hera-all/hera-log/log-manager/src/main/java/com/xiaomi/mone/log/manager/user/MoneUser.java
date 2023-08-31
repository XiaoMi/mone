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
package com.xiaomi.mone.log.manager.user;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/2 15:59
 */
@Data
@ToString
@Builder
public class MoneUser {

    /**
     * 用户名，如：zhangsan
     */
    @SerializedName("cas:user")
    private String user;

    private String fullUser;

    /**
     * 用户名，如：张三
     */
    @SerializedName("cas:name")
    private String name;

    /**
     * 用户展示名，如：sa zhang 张三
     */
    @SerializedName("cas:displayName")
    private String displayName;

    /**
     * 部门名称
     */
    @SerializedName("cas:departmentName")
    private String departmentName;

    /**
     * 邮箱
     */
    @SerializedName("cas:email")
    private String email;

    /**
     * miID
     */
    @SerializedName("cas:miID")
    private String miID;

    /**
     * miID
     */
    @SerializedName("cas:uid")
    private String uID;

    /**
     * 头像
     */
    @SerializedName("cas:avatar")
    private String avatar;

    private Boolean isAdmin;

    private String zone;
    /**
     * 最后一级部门Id
     */
    private String deptId;

    private String company;

    private Integer userType;

//    public String getSource(){
//        if(this.getDepartmentName().equals("研发效能不")
//    }
}
