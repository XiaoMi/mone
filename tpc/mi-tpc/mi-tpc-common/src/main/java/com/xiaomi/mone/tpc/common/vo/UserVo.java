package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 18:49
 */
@ToString
@Data
public class UserVo implements Serializable {
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
    private long createTime;
    private long updateTime;
    private String account;
    private boolean topMgr;
    private String showAccount;
}
