package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2023/8/7 10:54 上午
 */
@Data
public class ProjectAlarmInfo implements Serializable {

    private Integer iamId;
    private Integer iamType;
    private Integer projectId;

}
