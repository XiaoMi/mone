package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:12
 */
@ToString
@Data
public class NodeUserRelVo implements Serializable {
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
    private Long userId;
    private String account;
    private Integer userType;
    private Long nodeId;
    private Integer nodeType;
    private Integer tester;
    /**
     * 项目的成员数据
     */
    private long projectId;
    private int roleType;
}
