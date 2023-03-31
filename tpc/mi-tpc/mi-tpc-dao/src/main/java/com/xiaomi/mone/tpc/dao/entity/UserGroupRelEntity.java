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
@Table("user_group_rel_entity")
@Data
public class UserGroupRelEntity extends BaseEntity {

    @Column("group_id")
    private Long groupId;
    @Column("user_id")
    private Long userId;
    @Column
    private String account;
    @Column("user_type")
    private Integer userType;
}
