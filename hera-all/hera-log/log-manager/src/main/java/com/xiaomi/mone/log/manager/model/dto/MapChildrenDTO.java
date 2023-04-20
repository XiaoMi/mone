package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class MapChildrenDTO<K, V> {
    private K label;
    private V children;

    public MapChildrenDTO() {
    }

    public MapChildrenDTO(K label, V children) {
        this.label = label;
        this.children = children;
    }

    public static MapChildrenDTO Of(Object label, Object value) {
        return new MapChildrenDTO(label, value);
    }
}
