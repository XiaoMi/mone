package com.xiaomi.mone.tpc.common.enums;

import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.util.ListUtil;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.tpc.common.enums.NodeTypeEnum.*;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum ApplyTypeEnum implements Base {
    NODE_APPLY(0, "节点申请", ApplyAddNodeParam.class, ListUtil.list(TOP_TYPE,PRO_GROUP_TYPE, PRO_TYPE, PRO_SUB_GROUP), true),
    MEMBER_APPLY(1, "成员申请", ApplyAddMemberParam.class, ListUtil.list(TOP_TYPE,PRO_GROUP_TYPE,PRO_TYPE, PRO_SUB_GROUP), true),
    RESOURCE_POOL(2, "资源申请", ApplyResourcePoolParam.class, ListUtil.list(RES_GROUP_TYPE), true),
    OUTER_APPLY(3, "权限申请(一级审批)", ApplyOuterParam.class, ListUtil.list(PRO_SUB_GROUP), false),
    OUTER_APPLY_STEP2(4, "权限申请(二级审批)", ApplyOuterParam.class, ListUtil.list(PRO_SUB_GROUP), false),
    SYSTEM_APPLY(5, "系统申请", ApplyAddSystemParam.class, ListUtil.list(TOP_TYPE), false),
    ;
    private Integer code;
    private String desc;
    private Class<?> cls;
    private List<Integer> nodeTypes;
    private boolean pageShow;
    ApplyTypeEnum(Integer code, String desc, Class<?> cls, List<NodeTypeEnum> nodeTypes, boolean pageShow) {
        this.code = code;
        this.desc = desc;
        this.cls = cls;
        if (nodeTypes != null && !nodeTypes.isEmpty()) {
            this.nodeTypes = nodeTypes.stream().map(NodeTypeEnum::getCode).collect(Collectors.toList());
        }
        this.pageShow = pageShow;
    }

    public static final ApplyTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (ApplyTypeEnum userTypeEnum : ApplyTypeEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }

    /**
     * 页面展示
     * @return
     */
    public boolean isPageShow() {
        return pageShow;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public Class<?> getCls() {
        return cls;
    }

    public List<Integer> getNodeTypes() {
        return nodeTypes;
    }
}
