package com.xiaomi.youpin.gwdash.dao.model.transfer;

import com.xiaomi.youpin.gwdash.common.MetaDataRelationTypeEnum;
import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * goodjava@qq.com
 * 用来迁移数据用
 */
@Data
@Table("metadata_relation")
public class MetaDataRelationTransfer {

    @Id(auto = false)
    private int id;

    @Column
    private int source;

    @Column
    private int target;

    /**
     * @see MetaDataRelationTypeEnum
     */
    @Column
    private int type;

    public MetaDataRelationTransfer(){
    }

    public MetaDataRelationTransfer(int source, int target, int type){
        this.source = source;
        this.target = target;
        this.type = type;
    }
}
