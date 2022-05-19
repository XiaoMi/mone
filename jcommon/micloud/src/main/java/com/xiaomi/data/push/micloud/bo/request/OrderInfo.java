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
