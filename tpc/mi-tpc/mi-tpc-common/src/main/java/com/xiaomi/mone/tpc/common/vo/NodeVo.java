package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:12
 */
@ToString
@Data
public class NodeVo implements Serializable {
    private Long id;
    private String code;
    private Long outId;
    private Integer outIdType;
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
    private Long parentId;
    private Integer parentType;
    private Long topId;
    private Integer topType;
    private String nodeName;
    private Integer envFlag;
    private boolean topMgr;//是顶级管理员
    private boolean topMember;//是顶级成员
    private boolean parentMgr;//是父节点管理员
    private boolean parentMember;//是父节点成员
    private boolean currentMgr;//当前节点管理员
    private boolean currentMember;//成员
    private boolean projectTester;//项目测试人员
    private List<Integer> supportNodeTypes;//支持添加的子节点类型
    private List<Integer> supportOrgNodeTypes;//支持部门的节点类型
    private List<Integer> supportMemberNodeTypes;//支持成员的节点类型
    private List<Integer> supportMemberTypes;//支持添加的成员类型
    private List<Integer> supportApplyTypes;//支持申请的类型
    private OrgInfoVo orgInfoVo;//部门信息
    private boolean supportApply;//是否支持工单
    private boolean supportMove;//是否支持移动
    private boolean supportEditOrg;//支持编辑组织

    private boolean hasMemberList;
    private boolean memberListOper;

    private boolean hasUserGroupList;
    private boolean userGroupListOper;

    private boolean hasProMemberList;
    private boolean proMemberListOper;

    private boolean hasFlagList;
    private boolean flagListOper;

    private boolean hasUserNodeRoleList;
    private boolean userNodeRoleListOper;

    private boolean hasPoolResList;
    private boolean poolResListOper;

    private boolean hasRelResList;
    private boolean relResListOper;

    private boolean hasSubNodeList;
    private boolean subNodeListOper;

    private boolean hasIamList;
    private boolean iamListOper;

    private NodeVo parentNode;
    private Map<String,String> env;

}
