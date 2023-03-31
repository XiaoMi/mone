package com.xiaomi.mone.tpc.common.enums;

import com.xiaomi.mone.tpc.common.util.ListUtil;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 节点用户关系类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum NodeUserRelTypeEnum implements Base {
    MANAGER(0, "管理员", ListUtil.list(NodeTypeEnum.TOP_TYPE, NodeTypeEnum.PRO_GROUP_TYPE, NodeTypeEnum.PRO_TYPE, NodeTypeEnum.PRO_SUB_GROUP)),
    MEMBER(1, "成员", ListUtil.list(NodeTypeEnum.PRO_GROUP_TYPE, NodeTypeEnum.PRO_TYPE, NodeTypeEnum.PRO_SUB_GROUP)),
    ;
    private Integer code;
    private String desc;
    private List<Integer> nodeTypes;
    NodeUserRelTypeEnum(Integer mode, String desc, List<NodeTypeEnum> nodeTypes) {
        this.code = mode;
        this.desc = desc;
        if (nodeTypes != null && !nodeTypes.isEmpty()) {
            this.nodeTypes = nodeTypes.stream().map(NodeTypeEnum::getCode).collect(Collectors.toList());
        }
    }

    public static final NodeUserRelTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (NodeUserRelTypeEnum userTypeEnum : NodeUserRelTypeEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public List<Integer> getNodeTypes() {
        return nodeTypes;
    }
}
