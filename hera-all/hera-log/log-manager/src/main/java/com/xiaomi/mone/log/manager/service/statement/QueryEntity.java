package com.xiaomi.mone.log.manager.service.statement;

import com.xiaomi.mone.log.api.enums.EsOperatorMatchEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryEntity {
    /**
     * field name
     */
    private String field;
    /**
     * field value
     */
    private String fieldValue;

    private EsOperatorMatchEnum matchEnum;
}
