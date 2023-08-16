package com.xiaomi.youpin.docean.plugin.es.antlr4.common.context;

import com.xiaomi.youpin.docean.plugin.es.antlr4.common.enums.ValueTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/3 17:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValueContext {
    private ValueTypeEnum type;
    private Object value;
}
