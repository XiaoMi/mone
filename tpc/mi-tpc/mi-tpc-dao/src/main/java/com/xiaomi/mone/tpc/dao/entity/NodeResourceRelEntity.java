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
@Table("node_resource_rel_entity")
@Data
public class NodeResourceRelEntity extends BaseEntity {

    @Column("resource_id")
    private Long resourceId;
    @Column("resource_type")
    private Integer resourceType;
    @Column("node_id")
    private Long nodeId;
    @Column("node_type")
    private Integer nodeType;
}
