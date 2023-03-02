package com.xiaomi.mone.log.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/26 15:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryDTO<T> {
    private T value;
    private String label;
    private List<DictionaryDTO> children;

    public static DictionaryDTO Of(Object label, String value) {
        return new DictionaryDTO(label, value, null);
    }
}
