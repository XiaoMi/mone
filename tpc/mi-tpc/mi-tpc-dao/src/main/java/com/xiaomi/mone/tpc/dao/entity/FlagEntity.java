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
@Table("flag_entity")
@Data
public class FlagEntity extends BaseEntity{

    @Column("parent_id")
    private Long parentId;

    @Column("flag_name")
    private String flagName;

    @Column("flag_key")
    private String flagKey;

    @Column("flag_val")
    private String flagVal;

}
