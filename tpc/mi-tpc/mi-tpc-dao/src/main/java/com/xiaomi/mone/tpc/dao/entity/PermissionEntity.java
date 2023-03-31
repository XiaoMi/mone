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
@Table("permission_entity")
@Data
public class PermissionEntity extends BaseEntity{

    @Column("system_id")
    private Long systemId;

    @Column("permission_name")
    private String permissionName;

    @Column("path")
    private String path;

}
