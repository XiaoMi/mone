package com.xiaomi.mone.tpc.cache.enums;

import lombok.ToString;

import java.util.concurrent.TimeUnit;

/**
 * 类型枚举
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@ToString
public enum ModuleEnum {
    TEST(1, "测试使用", 1, 1, TimeUnit.HOURS),
    USER_ACC_TYPE(100, "用户-账号-类型", 2, 1, TimeUnit.DAYS),
    USER_ACC_TYPE_LOCK(101, "用户-账号-类型锁", 1, 1, TimeUnit.DAYS),
    SYSTEM_NAME(110, "系统-名称", 1, 1, TimeUnit.DAYS),
    PERMISSION_PATH(120, "权限-路径", 1, 1, TimeUnit.DAYS),
    NODE(130, "节点", 1, 1, TimeUnit.DAYS),
    NODE_CODE_LOCK(131, "节点编码锁", 1, 5, TimeUnit.SECONDS),
    NODE_PRO_ADD_LOCK(132, "节点-项目-新增-锁", 1, 5, TimeUnit.SECONDS),
    USER_GROUP_REL(140, "用户组关系", 1, 1, TimeUnit.DAYS),
    USER_GROUP_REL_LIST_UID(141, "用户关联的组列表-用户ID", 1, 1, TimeUnit.DAYS),
    ROLE_PERMISSION_REL_LIST_RID(150, "角色权限关系-角色ID", 1, 1, TimeUnit.DAYS),
    ROLE(160, "角色", 1, 1, TimeUnit.DAYS),
    USER_NODE_ROLE_REL_USER(170, "用户节点角色关系-用户", 1, 1, TimeUnit.DAYS),
    USER_NODE_ROLE_REL_NODEID(171, "用户节点角色关系-解析", 1, 1, TimeUnit.DAYS),
    APPLY_APPROVAL_LOCK(180, "工单审核锁", 1, 20, TimeUnit.SECONDS),
    ORG_INFO(190, "部门缓存", 2, 1, TimeUnit.DAYS),
    ORG_INFO_LIST(191, "部门列表缓存", 2, 1, TimeUnit.DAYS),
    ;
    private final Integer code;
    private final String desc;
    private final int version;
    private int time;
    private TimeUnit unit;

    ModuleEnum(Integer mode, String desc, int version, int time, TimeUnit unit) {
        this.code = mode;
        this.desc = desc;
        this.version = version;
        this.time = time;
        this.unit = unit;
    }

    public static final ModuleEnum getEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (ModuleEnum userTypeEnum : ModuleEnum.values()) {
            if (code.equals(userTypeEnum.code)) {
                return userTypeEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public int getVersion() {
        return version;
    }

    public int getTime() {
        return time;
    }

    public TimeUnit getUnit() {
        return unit;
    }
}
