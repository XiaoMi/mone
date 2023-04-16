package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class MapDTO<K, V> {
    private K label;
    private V value;
    private String key;

    public MapDTO() {
    }

    public MapDTO(K label, V value) {
        this.label = label;
        this.value = value;
    }

    public MapDTO(K label, V value, String key) {
        this.label = label;
        this.value = value;
        this.key = key;
    }

    public static MapDTO Of(Object label, Object value) {
        return new MapDTO(label, value);
    }

    public static MapDTO Of(Object label, Object value, String key) {
        return new MapDTO(label, value, key);
    }
}