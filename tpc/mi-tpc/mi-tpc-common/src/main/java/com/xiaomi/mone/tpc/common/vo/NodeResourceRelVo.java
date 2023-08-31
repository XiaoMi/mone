package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @project: mi-tpc
 * @author: zhangxiaowei
 * @date: 2022/3/25
 */
@ToString
@Data
public class NodeResourceRelVo implements Serializable {
    private Long id;
    private Integer type;
    private Integer status;
    private String desc;
    private String content;
    private Long createrId;
    private String createrAcc;
    private Integer createrType;
    private Long updaterId;
    private String updaterAcc;
    private Integer updaterType;
    private Long createTime;
    private Long updateTime;
    private Long resourceId;
    private Integer resourceType;
    private Long nodeId;
    private Integer nodeType;
}
