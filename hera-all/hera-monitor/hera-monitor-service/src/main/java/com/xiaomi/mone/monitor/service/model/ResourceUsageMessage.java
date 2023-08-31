package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2022/5/12 3:38 下午
 */
@Data
public class ResourceUsageMessage implements Serializable {

    String ip;
    String projectId;
    String projectName;
    String cpuUsage;
    String memUsage;
    List<String> members;
    String value;

    public ResourceUsageMessage(String ip,String projectId,String projectName,String cpuUsage,String memUsage,List<String> members,String value){
        this.ip = ip;
        this.projectId = projectId;
        this.projectName = projectName;
        this.cpuUsage = cpuUsage;
        this.memUsage = memUsage;
        this.members = members;
        this.value = value;
    }

}
