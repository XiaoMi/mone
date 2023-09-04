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
package com.xiaomi.mone.log.manager.model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @Auther: wtt
 * @Date: 2022/3/24 10:36
 * @Description:
 */
@Data
public class RadarAppInfoDTO {
    private Long id;
    private String name;
    private List<Member> members;
    private String createTime;
    private String updateTime;
    private boolean joined;


    @Data
    public static class Member {
        @SerializedName(value = "user_id")
        private String userId;
        @SerializedName(value = "user_name")
        private String userName;
    }
}
