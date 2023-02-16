package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/2/14 10:55
 */
@Data
public class K8sMachineChangeDTO {
    private Long appId;
    private Long appType;
    private Long iamTreeId;
    private String appName;
    private Long envId;
    private String envName;
    private List<String> changedMachines;
    /**
     * k8s中删除的pod,每个机器发过去的会多但是不会少
     */
    private List<String> deletingMachines;
}
