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
package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.LogStoreDTO;
import com.xiaomi.mone.log.manager.model.dto.MapDTO;
import com.xiaomi.mone.log.manager.model.dto.MenuDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.vo.LogStoreParam;

import java.util.List;
import java.util.Map;

public interface LogStoreService {

    Result<String> newLogStore(LogStoreParam cmd);

    MilogLogStoreDO buildLogStoreEsInfo(LogStoreParam cmd, String creator);

    Result<LogStoreDTO> getLogStoreById(Long id);

    Result<List<MapDTO<String, Long>>> getLogStoreBySpaceId(Long spaceId);

    Result<Map<String, Object>> getLogStoreByPage(String logstoreName, Long spaceId, int page, int pagesize);

    Result<Map<String, Object>> getAllLogStore();

    Result<List<MilogLogStoreDO>> getLogStoreByIds(List<Long> ids);

    Result<String> updateLogStore(LogStoreParam cmd);

    Result<Void> deleteLogStore(Long id);

    Result<List<Map<String, String>>> getStoreIps(Long storeId);

    Result<List<MenuDTO<Long, String>>> queryDeptExIndexList(String regionCode, Integer logTypeCode);

}
