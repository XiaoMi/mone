package com.xiaomi.youpin.gwdash.common;

public enum MetaDataTypeEnum {
    APiGroupCluster(1),
    Domain(2);

    private int type;

    MetaDataTypeEnum(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }
}
