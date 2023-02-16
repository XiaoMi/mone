package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class MapDTO<K, V> {
    private K label;
    private V value;

    public MapDTO() {
    }

    public MapDTO(K label, V value) {
        this.label = label;
        this.value = value;
    }

    public static MapDTO Of(Object label, Object value) {
        return new MapDTO(label, value);
    }
}
