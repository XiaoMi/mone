package com.xiaomi.mone.monitor.service.model.project.group;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2023/6/7 11:15 上午
 */
@Data
public class ProjectGroupRequest implements Serializable {

    String user;
    Integer groupType;
    Integer projectGroupId;
    String projectGroupName;
    String appName;
    Integer level;
    Integer page;
    Integer pageSize;

}
