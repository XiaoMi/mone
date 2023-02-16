package com.xiaomi.mone.monitor.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/9/14 3:17 下午
 */
@Data
public class Pair<T extends Object> implements Serializable {

    T value;
    String label;

    public Pair(T value,String label){
        this.label = label;
        this.value = value;
    }
}
