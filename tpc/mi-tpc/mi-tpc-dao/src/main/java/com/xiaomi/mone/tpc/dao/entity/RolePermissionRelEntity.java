package com.xiaomi.mone.tpc.dao.entity;

import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 9:39
 */
@ToString(callSuper = true)
@Table("role_permission_rel_entity")
@Data
public class RolePermissionRelEntity extends BaseEntity{

    @Column("system_id")
    private Long systemId;

    @Column("permission_id")
    private Long permissionId;

    @Column("role_id")
    private Long roleId;

}
