package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class ValueDTO<V> {
    private V value;

    public ValueDTO() {
    }

    public ValueDTO(V value) {
        this.value = value;
    }

    public static ValueDTO Of(Object label, Object value) {
        return new ValueDTO(value);
    }
}
