package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/9/15 7:01 下午
 */
@Data
public class AppRulesQuery implements Serializable {
    private String appName;
    private Integer page;
    private Integer pageSize;
}
