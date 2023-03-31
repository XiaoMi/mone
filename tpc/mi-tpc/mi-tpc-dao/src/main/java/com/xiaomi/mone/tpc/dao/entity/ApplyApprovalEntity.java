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
@Table("apply_approval_entity")
@Data
public class ApplyApprovalEntity extends BaseEntity{

    @Column("apply_id")
    private Long applyId;

    @Column("apply_type")
    private Integer applyType;

    @Column("cur_node_id")
    private Long curNodeId;

    @Column("cur_node_type")
    private Integer curNodeType;

    @Column("approval_name")
    private String approvalName;

}
