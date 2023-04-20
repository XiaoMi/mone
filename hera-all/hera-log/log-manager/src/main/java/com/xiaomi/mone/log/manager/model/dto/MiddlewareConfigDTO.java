package com.xiaomi.mone.log.manager.model.dto;

import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/21 16:07
 */
@Data
public class MiddlewareConfigDTO extends MilogMiddlewareConfig {
    private List<?> types;
}
