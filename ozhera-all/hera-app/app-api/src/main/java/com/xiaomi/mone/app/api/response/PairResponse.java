package com.xiaomi.mone.app.api.response;

import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 15:01
 */
@Data
public class PairResponse<K, V> {

    private K label;
    private V value;

    public PairResponse() {
    }

    public PairResponse(K label, V value) {
        this.label = label;
        this.value = value;
    }

}
