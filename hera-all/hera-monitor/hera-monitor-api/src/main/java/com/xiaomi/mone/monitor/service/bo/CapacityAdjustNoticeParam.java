package com.xiaomi.mone.monitor.service.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2022/12/1 4:23 下午
 */
@Data
public class CapacityAdjustNoticeParam implements Serializable {

    //容量调整类型：扩容/缩容
    private CapacityAdjustType adjustType;

    private Long projectId;

    private Integer envId;

    private Integer beforeAdjustNum;

    private Integer afterAdjustNum;

    private CapacityAdjustCause adjustCause;

    private String value;//触发扩容的指标阈值

    private Long time;//触发扩容的指标阈值


}
