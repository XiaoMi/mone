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
@Table("role_entity")
@Data
public class RoleEntity extends BaseEntity{

    @Column("system_id")
    private Long systemId;

    @Column("role_name")
    private String roleName;

    @Column("node_id")
    private Long nodeId;

    @Column("node_type")
    private Integer nodeType;

}
