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
@Table("resource_entity")
@Data
public class ResourceEntity extends BaseEntity{

    @Column("pool_node_id")
    private Long poolNodeId;

    @Column("apply_id")
    private Long applyId;

    @Column("resource_name")
    private String resourceName;

    @Column("key1")
    private String key1;

    @Column("key2")
    private String key2;

    @Column("env_flag")
    private Integer envFlag;

    @Column("is_open_kc")
    private Integer isOpenKc;

    @Column("sid")
    private String sid;

    @Column("kc_user")
    private String kcUser;

    @Column("mfa")
    private String mfa;

    @Column("region")
    private Integer region;

}
