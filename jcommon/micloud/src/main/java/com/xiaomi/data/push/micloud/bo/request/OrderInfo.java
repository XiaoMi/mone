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

package com.xiaomi.data.push.micloud.bo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xiaomi.data.push.micloud.bo.request.Disk;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderInfo implements Serializable {

    @JsonProperty("nodegroup")
    private String nodegroup;

    @JsonProperty("type_id")
    private String typeId;

    @JsonProperty("suit_id")
    private String suitId;

    @JsonProperty("site_id")
    private String siteId;

    @JsonProperty("image_id")
    private String imageId;

    @JsonProperty("net_id")
    private String netId;

    @JsonProperty("disk")
    private Disk disk;

    @JsonProperty("zones")
    private List<String> zones;

    @JsonProperty("machine_num")
    private List<Integer> machineNum;

    @JsonProperty("performance_calculation")
    private String performanceCalculation;

    @JsonProperty("project_context")
    private String projectContext;

    @JsonProperty("cover_time")
    private int coverTime;

    @JsonProperty("user_name")
    private String username;

    @JsonProperty("service_name")
    private String serviceName;

    @JsonProperty("manager_name")
    private String managerName;

    @JsonProperty("manager_email")
    private String managerEmail;

    @JsonProperty("department")
    private String department;
}
