package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class EnumData<K, V> implements Serializable {
    private K k;
    private V v;

    public EnumData(){}

    public EnumData(K k, V v){
        this.k = k;
        this.v = v;
    }
}
