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
@Table("apply_entity")
@Data
public class ApplyEntity extends BaseEntity{


    @Column("cur_node_id")
    private Long curNodeId;

    @Column("cur_node_type")
    private Integer curNodeType;

    @Column("apply_node_id")
    private Long applyNodeId;

    @Column("apply_node_type")
    private Integer applyNodeType;

    @Column("apply_user_id")
    private Long applyUserId;

    @Column("apply_account")
    private String applyAccount;

    @Column("apply_user_type")
    private Integer applyUserType;

    @Column("apply_name")
    private String applyName;

}
