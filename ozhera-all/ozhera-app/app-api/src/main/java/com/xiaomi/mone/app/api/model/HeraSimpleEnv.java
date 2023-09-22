package com.xiaomi.mone.app.api.model;

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
 * @date 2022/11/12 11:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeraSimpleEnv implements Serializable {
    /**
     * 当前主键ID
     */
    private Long id;
    /**
     * 环境名称
     */
    private String name;
    /**
     * 部署的ip集合
     */
    private List<String> ips;
}
