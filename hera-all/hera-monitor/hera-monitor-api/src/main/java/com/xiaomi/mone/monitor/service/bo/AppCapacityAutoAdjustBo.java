package com.xiaomi.mone.monitor.service.bo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Data
@ToString
public class AppCapacityAutoAdjustBo implements Serializable {

    private Integer appId;//应用id

    private Integer pipelineId;//流水线id

    private String container;//容器名称

    private Integer minInstance;//最小实例数

    private Integer maxInstance;//最大实例数

    private Integer autoCapacity;//是否自动扩容 1 是，0否

    private Integer dependOn;//扩容依据 0 cpu 1内存 2cpu及内存

}