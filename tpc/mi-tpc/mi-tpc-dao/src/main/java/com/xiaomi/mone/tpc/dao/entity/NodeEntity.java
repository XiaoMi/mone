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
@Table("node_entity")
@Data
public class NodeEntity extends BaseEntity{

    @Column("parent_id")
    private Long parentId;

    @Column("parent_type")
    private Integer parentType;

    @Column("top_id")
    private Long topId;

    @Column("top_type")
    private Integer topType;

    @Column("node_name")
    private String nodeName;

    @Column("out_id")
    private Long outId;

    @Column("out_id_type")
    private Integer outIdType;

    @Column("env_flag")
    private Integer envFlag;

    @Column(value = "code", wrap = true)
    private String code;

    @Column(value = "env")
    private String env;

}
