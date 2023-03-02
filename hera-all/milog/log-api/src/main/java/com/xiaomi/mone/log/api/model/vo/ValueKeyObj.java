package com.xiaomi.mone.log.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/5/11 15:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValueKeyObj<T> {
    private T key;
    private String value;
}
