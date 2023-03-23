package com.xiaomi.mone.log.manager.service.statement;

import com.xiaomi.mone.log.api.enums.EsOperatorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperatorData {
    private EsOperatorEnum operatorEnum;
    private List<String> messageList;
}
