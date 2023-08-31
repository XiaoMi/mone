package com.xiaomi.mone.monitor.service.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2022/6/30 2:58 下午
 */
@Data
@ToString
public class CapacityAdjustRecordRequest implements Serializable {
    Integer appId;
    Integer page;
    Integer pageSize;
}
