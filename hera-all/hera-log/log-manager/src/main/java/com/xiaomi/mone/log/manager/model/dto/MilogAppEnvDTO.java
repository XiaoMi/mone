package com.xiaomi.mone.log.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/8/19 11:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilogAppEnvDTO implements Serializable {
    /**
     * 环境名称
     */
    private String label;
    /**
     * 环境主键
     */
    private Long value;
    /**
     * 环境下的ips集合
     */
    private List<String> ips;
}
