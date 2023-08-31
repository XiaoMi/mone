/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
