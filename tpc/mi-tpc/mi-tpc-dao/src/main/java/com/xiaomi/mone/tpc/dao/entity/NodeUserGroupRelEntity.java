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
@Table("node_user_group_rel_entity")
@Data
public class NodeUserGroupRelEntity extends BaseEntity {

    @Column("user_group_id")
    private Long userGroupId;
    @Column("node_id")
    private Long nodeId;
    @Column("node_type")
    private Integer nodeType;

}
