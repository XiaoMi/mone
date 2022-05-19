package com.xiaomi.data.push.client;

import lombok.Data;

/**
 * @author goodjava@qq.com
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
