package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/6/28 16:12
 */
@ToString
@Data
public class IamResInfoVo implements Serializable {

    private Long id;
    private String createTime;
    private String updateTime;
    private Long treeId;
    private Long serviceId;
    private String serviceName;
    private String resourceType;
    private String resourceId;
    private String resourceName;
    private String region;
    private String env;
    private List<IamResInfoVo> data;
    private int count;
    private int limit;
    private int offset;
}
