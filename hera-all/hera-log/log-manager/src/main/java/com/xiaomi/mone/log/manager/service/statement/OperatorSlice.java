package com.xiaomi.mone.log.manager.service.statement;

import com.xiaomi.mone.log.api.enums.EsOperatorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperatorSlice {
    private EsOperatorEnum operatorEnum;
    private String message;

}
