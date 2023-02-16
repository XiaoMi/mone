package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/9/22 10:36
 */
@Data
public class MenuDTO<K, V> {
    private K key;
    private V label;
    private List<MenuDTO<K, V>> children;
}
