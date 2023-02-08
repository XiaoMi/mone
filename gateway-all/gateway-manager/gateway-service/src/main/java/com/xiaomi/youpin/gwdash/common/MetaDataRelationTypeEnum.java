package com.xiaomi.youpin.gwdash.common;

public enum MetaDataRelationTypeEnum {
    ApiGroupCluster2ApiGroup(1),
    ApiGroupCluster2Domain(2);

    private int type;

    MetaDataRelationTypeEnum(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }
}
