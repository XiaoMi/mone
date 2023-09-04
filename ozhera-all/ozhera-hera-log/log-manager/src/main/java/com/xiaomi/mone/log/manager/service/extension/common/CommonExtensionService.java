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
package com.xiaomi.mone.log.manager.service.extension.common;

import com.xiaomi.mone.log.manager.model.vo.LogQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/27 16:13
 */
public interface CommonExtensionService {

    String DEFAULT_COMMON_EXTENSION_SERVICE_KEY = "defaultCommonExtensionService";


    String getLogManagePrefix();

    String getHeraLogStreamServerName();

    String getMachineRoomName(String machineRoomEn);

    boolean middlewareEnumValid(Integer type);

    BoolQueryBuilder commonRangeQuery(LogQuery logQuery);

    TermQueryBuilder multipleChooseBuilder(Long storeId, String chooseVal);

    String queryDateHistogramField(Long storeId);

    String getSearchIndex(Long logStoreId, String esIndex);
}
