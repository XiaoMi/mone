package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:12
 */
@ToString
@Data
public class ApplyVo implements Serializable {
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
    private Long curNodeId;
    private Integer curNodeType;
    private Long applyNodeId;
    private Integer applyNodeType;
    private Long applyUserId;
    private String applyAccount;
    private Integer applyUserType;
    private String applyName;
    private boolean resubmit;//是否可重提单
    private boolean approval;//是否可审批
    private boolean close;//关闭
    private boolean recall;//撤回
    private List<String> approvalAccs;//审核人列表
}
