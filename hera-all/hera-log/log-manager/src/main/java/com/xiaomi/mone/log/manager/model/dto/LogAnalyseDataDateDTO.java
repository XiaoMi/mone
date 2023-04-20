package com.xiaomi.mone.log.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogAnalyseDataDateDTO extends LogAnalyseDataDTO {

// 数据结构
//    data:
//        [
//            ['2022-10-22', 44, 55, 66, 2],
//            ['2022-10-23', 6, 16, 23, 1],
//            ['2022-10-24', 6, 16, 23, 1],
//            ['2022-10-25', 6, 16, 23, 1]
//        ]
//    type:
//        ['error', 'info', 'close', 'open']

    private List<List<String>> data;

    private Set<String> type;
}
