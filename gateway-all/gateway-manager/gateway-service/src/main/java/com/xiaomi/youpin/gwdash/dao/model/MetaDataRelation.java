package com.xiaomi.youpin.gwdash.dao.model;

import com.xiaomi.youpin.gwdash.common.MetaDataRelationTypeEnum;
import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Data
@Table("metadata_relation")
public class MetaDataRelation {
    @Id
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

    public MetaDataRelation(){
    }

    public MetaDataRelation(int source,int target,int type){
        this.source = source;
        this.target = target;
        this.type = type;
    }
}
