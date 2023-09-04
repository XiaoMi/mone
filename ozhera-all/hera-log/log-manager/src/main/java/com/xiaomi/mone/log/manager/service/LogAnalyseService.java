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
import com.xiaomi.mone.log.manager.model.dto.DashboardDTO;
import com.xiaomi.mone.log.manager.model.dto.GraphDTO;
import com.xiaomi.mone.log.manager.model.dto.GraphTypeDTO;
import com.xiaomi.mone.log.manager.model.dto.LogAnalyseDataDTO;
import com.xiaomi.mone.log.manager.model.vo.*;

import java.io.IOException;
import java.util.List;

 public interface LogAnalyseService {

    Result<DashboardDTO> getDashboardGraph(LogAnalyseQuery logAnalyseQuery);

    Result<Long> createGraph(CreateGraphCmd cmd);

    Result<Boolean> updateGraph(UpdateGraphCmd cmd);

    Result<Boolean> deleteGraph(Long graphId);

    Result<List<GraphDTO>> searchGraph(GraphQuery query);

    Result<Boolean> ref(DGRefCmd cmd);

    Result<Boolean> delRef(DGRefDelCmd cmd);

    Result<Boolean> updateRef(DGRefUpdateCmd cmd);

    Result<LogAnalyseDataDTO> data(LogAnalyseDataQuery query) throws IOException;

    Result<LogAnalyseDataDTO> dataPre(LogAnalyseDataPreQuery query) throws IOException;

    Result<Long> createDashboard(CreateDashboardCmd cmd);

    Result<List<GraphTypeDTO>> type();

    Result<List<String>> supportKye(Long storeId);
}
