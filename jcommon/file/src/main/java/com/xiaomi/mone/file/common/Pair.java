package com.xiaomi.mone.file.common;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/9/26 10:50
 */
@Data
public class Pair<K, V> {

    private K key;

    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }


}
