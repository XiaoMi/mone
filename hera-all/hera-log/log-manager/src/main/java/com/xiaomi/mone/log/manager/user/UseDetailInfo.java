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
import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/6 11:34
 */
@Data
public class UseDetailInfo {

    private String uid;
    private String personId;
    private String originalPersonId;
    private String name;
    private String displayName;
    private String type;
    private String userName;
    private String email;
    private String sex;
    private String headUrl;
    private String deptId;
    @SerializedName("deptDescr")
    private String deptDesc;
    @SerializedName("fullDeptDescr")
    private String fullDeptDesc;
    private List<DeptDescriptor> fullDeptDescriptorList;
    private String company;
    @SerializedName("companyDescr")
    private String companyDesc;
    private String hrStatus;
    private String source;


    @Data
    public static class DeptDescriptor {
        private String deptEnName;
        private String deptId;
        private String deptName;
        private Integer level;
    }

}
