package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangxiaowei6
 * @date 2021/12/27  下午
 */
@Data
public class ServiceQps implements Serializable{
    private String type;
    private String avgQps;
    private String TotalQps;
}
