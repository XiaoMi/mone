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
@Table("user_node_role_rel_entity")
@Data
public class UserNodeRoleRelEntity extends BaseEntity{

    @Column("user_id")
    private Long userId;

    @Column("account")
    private String account;

    @Column("user_type")
    private Integer userType;

    @Column("node_id")
    private Long nodeId;

    @Column("node_type")
    private Integer nodeType;

    @Column("role_id")
    private Long roleId;

    @Column("system_id")
    private Long systemId;

}
