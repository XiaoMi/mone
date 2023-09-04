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
package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.api.model.vo.AgentLogProcessDTO;
import com.xiaomi.mone.log.api.model.vo.TailLogProcessDTO;
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.manager.model.cache.LogCellectProcessCache;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogProcessDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LogProcessConvert {
    LogProcessConvert INSTANCE = Mappers.getMapper(LogProcessConvert.class);

    AgentLogProcessDTO fromDO(MilogLogProcessDO milogLogProcessDO);

//    @Mappings({
//            @Mapping(source = "collectTime", target = "collectTime")
//    })
    AgentLogProcessDTO collectProcessToDTO(UpdateLogProcessCmd.CollectDetail collectDetail);

    List<AgentLogProcessDTO> collectProcessToDTOList(List<UpdateLogProcessCmd.CollectDetail> collectDetailList);

    LogCellectProcessCache cmdToCache(UpdateLogProcessCmd.CollectDetail collectDetail);

    List<LogCellectProcessCache> cmdToCacheList(List<UpdateLogProcessCmd.CollectDetail> collectDetailList);

    AgentLogProcessDTO cacheToAgentDTO(LogCellectProcessCache cache);

    List<AgentLogProcessDTO> cacheToAgentDTOList(List<LogCellectProcessCache> cacheList);

    @Mappings({
            @Mapping(source = "ip", target = "ip"),
            @Mapping(source = "tailName", target = "tailName"),
    })
    TailLogProcessDTO cacheToTailDTO(LogCellectProcessCache cache, String ip, String tailName);

}
