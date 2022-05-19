package com.xiaomi.youpin.docean.common;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2020/6/24
 */
@Data
public class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Pair() {
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

}
