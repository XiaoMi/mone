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

    private boolean showDeploymentType;
    private boolean showEnvGroup;
    private boolean showMachineType;
    private boolean showMachineRegion;
    private boolean showServiceIp;
    private boolean showDeploymentSpace;
    private boolean showMqConfig;

    private List<DictionaryDTO> children;

    public DictionaryDTO(T value, String label, List<DictionaryDTO> children) {
        this.value = value;
        this.label = label;
        this.children = children;
    }

    public static DictionaryDTO Of(Object label, String value) {
        return new DictionaryDTO(label, value, null);
    }
}
