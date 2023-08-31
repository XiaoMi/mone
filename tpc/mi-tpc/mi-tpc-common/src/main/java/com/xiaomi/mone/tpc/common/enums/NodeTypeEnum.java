package com.xiaomi.mone.tpc.common.enums;

import com.xiaomi.mone.tpc.common.util.ListUtil;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 节点类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
    public enum NodeTypeEnum implements Base {
    TOP_TYPE(1, "根目录", ListUtil.list(3)),
    PRO_GROUP_TYPE(3, "项目组", ListUtil.list(4)),
    PRO_TYPE(4, "项目",ListUtil.list(5, 6, 7)),
    PART_TYPE(5, "部署空间",ListUtil.list(6)),
    RES_GROUP_TYPE(6, "环境", null),
    PRO_SUB_GROUP(7, "业务空间", ListUtil.list(7)),
    ;
    private Integer code;
    private String desc;
    private List<Integer> subNodeTypes;
    NodeTypeEnum(Integer code, String desc, List<Integer> subNodeTypes) {
        this.code = code;
        this.desc = desc;
        this.subNodeTypes = subNodeTypes;
    }

    /**
     * 支持资源池的节点类型
     * @param nodeType
     * @return
     */
    public static boolean supportResPoolNode(Integer nodeType) {
        return PRO_GROUP_TYPE.getCode().equals(nodeType)
                || TOP_TYPE.getCode().equals(nodeType)
                || PRO_SUB_GROUP.getCode().equals(nodeType);
    }

    /**
     * 支持成员的节点类型
     * @param nodeType
     * @return
     */
    public static boolean supportMemberNode(Integer nodeType) {
        for (NodeUserRelTypeEnum relTypeEnum : NodeUserRelTypeEnum.values()) {
            if (relTypeEnum.getNodeTypes() == null || relTypeEnum.getNodeTypes().isEmpty()) {
                continue;
            }
            if (relTypeEnum.getNodeTypes().contains(nodeType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 支持部门信息的节点
     * @param type
     * @return
     */
    public static boolean supportOrgNode(Integer type) {
        return getSupportOrgNode().contains(type);
    }

    /**
     * 支持部门信息的节点
     * @return
     */
    public static List<Integer> getSupportOrgNode() {
        List<Integer> list = new ArrayList<>();
        list.add(NodeTypeEnum.TOP_TYPE.getCode());
        list.add(NodeTypeEnum.PRO_GROUP_TYPE.getCode());
        list.add(NodeTypeEnum.PRO_TYPE.getCode());
        list.add(NodeTypeEnum.PRO_SUB_GROUP.getCode());
        return list;
    }

    /**
     * 支持iam信息的节点
     * @param type
     * @return
     */
    public static boolean supportIamNode(Integer type) {
        return getSupportIamNode().contains(type);
    }

    /**
     * 支持iam信息的节点
     * @return
     */
    public static List<Integer> getSupportIamNode() {
        List<Integer> list = new ArrayList<>();
        list.add(NodeTypeEnum.TOP_TYPE.getCode());
        list.add(NodeTypeEnum.PRO_GROUP_TYPE.getCode());
        list.add(NodeTypeEnum.PRO_TYPE.getCode());
        return list;
    }

    /**
     * 支持授权用户的节点
     * @param type
     * @return
     */
    public static boolean supportGrantUserNode(Integer type) {
        return NodeTypeEnum.TOP_TYPE.getCode().equals(type)
                || NodeTypeEnum.PRO_GROUP_TYPE.getCode().equals(type)
                || NodeTypeEnum.PRO_TYPE.getCode().equals(type)
                || NodeTypeEnum.PRO_SUB_GROUP.getCode().equals(type);
    }

    /**
     * 支持资源的节点
     * @param nodeType
     * @return
     */
    public static boolean supportResNodeType(Integer nodeType) {
        return NodeTypeEnum.RES_GROUP_TYPE.getCode().equals(nodeType);
    }

    public static final NodeTypeEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (NodeTypeEnum userTypeEnum : NodeTypeEnum.values()) {
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

    public List<Integer> getSubNodeTypes() {
        return subNodeTypes;
    }

    public List<Integer> getNodeUserTypes() {
        List<Integer> list = new ArrayList<>();
        for (NodeUserRelTypeEnum nodeUserRelTypeEnum : NodeUserRelTypeEnum.values()) {
            if (nodeUserRelTypeEnum.getNodeTypes() == null || !nodeUserRelTypeEnum.getNodeTypes().contains(code)) {
                continue;
            }
            list.add(nodeUserRelTypeEnum.getCode());
        }
        return list;
    }

    public boolean supportNodeUserGroupTypes() {
        List<Integer> nodeTypes = NodeUserRelTypeEnum.MEMBER.getNodeTypes();
        return nodeTypes != null && nodeTypes.contains(code);
    }

    public static List<Integer> getSupportMemberNode() {
        Set<Integer> set = new HashSet<>();
        for (NodeUserRelTypeEnum nodeUserRelTypeEnum : NodeUserRelTypeEnum.values()) {
            if (nodeUserRelTypeEnum.getNodeTypes() == null) {
                continue;
            }
            set.addAll(nodeUserRelTypeEnum.getNodeTypes());
        }
        return new ArrayList<>(set);
    }

    /**
     * 支持的工单类型列表
     * @return
     */
    public List<Integer> getApplyTypes() {
        List<Integer> list = new ArrayList<>();
        for (ApplyTypeEnum applyTypeEnum : ApplyTypeEnum.values()) {
            if (!applyTypeEnum.isPageShow()) {
                continue;
            }
            if (applyTypeEnum.getNodeTypes() == null || !applyTypeEnum.getNodeTypes().contains(code)) {
                continue;
            }
            list.add(applyTypeEnum.getCode());
        }
        return list;
    }

    /**
     * 支持的子节点类型
     * @param code
     * @return
     */
    public boolean supportSubNodeType(int code) {
        if (subNodeTypes == null) {
            return false;
        }
        return subNodeTypes.contains(code);
    }

    /**
     * 支持的节点成员类型
     * @param type
     * @return
     */
    public boolean supportNodeUserType(int type) {
        NodeUserRelTypeEnum nodeUserRelType = NodeUserRelTypeEnum.getEnum(type);
        if (nodeUserRelType == null) {
            return false;
        }
        List<Integer> nodeTypes = nodeUserRelType.getNodeTypes();
        if (nodeTypes == null) {
            return false;
        }
        return nodeTypes.contains(code);
    }

    /**
     * 支持的节点工单类型
     * @param type
     * @return
     */
    public boolean supportApplyType(int type) {
        ApplyTypeEnum applyType = ApplyTypeEnum.getEnum(type);
        if (applyType == null) {
            return false;
        }
        List<Integer> nodeTypes = applyType.getNodeTypes();
        if (nodeTypes == null) {
            return false;
        }
        return nodeTypes.contains(code);
    }

}
