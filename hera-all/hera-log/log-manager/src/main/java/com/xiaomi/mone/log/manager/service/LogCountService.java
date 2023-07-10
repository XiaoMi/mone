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
import com.xiaomi.mone.log.manager.model.dto.LogtailCollectTopDTO;
import com.xiaomi.mone.log.manager.model.dto.LogtailCollectTrendDTO;
import com.xiaomi.mone.log.manager.model.dto.SpaceCollectTopDTO;
import com.xiaomi.mone.log.manager.model.dto.SpaceCollectTrendDTO;

import java.io.IOException;
import java.util.List;

public interface LogCountService {

    Result<List<LogtailCollectTopDTO>> collectTop();

    Result<List<LogtailCollectTrendDTO>> collectTrend(Long tailId);

    void collectTopCount();

    void collectTrendCount(Long tailId);

    void collectLogCount(String thisDay) throws IOException;

    boolean isLogtailCountDone(String day);

    void deleteHistoryLogCount();

    void collectLogDelete(String day);

    void collectTrendRefresh();

    void showLogCountCache();

    Result<List<SpaceCollectTopDTO>> collectSpaceTop();

    void collectSpaceTopCount();

    void collectSpaceTrend();

    Result<List<SpaceCollectTrendDTO>> spaceCollectTrend(Long spaceId);

}
