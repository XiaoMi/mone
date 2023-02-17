package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.manager.model.cache.LogCellectProcessCache;
import com.xiaomi.mone.log.manager.model.dto.AgentLogProcessDTO;
import com.xiaomi.mone.log.manager.model.dto.TailLogProcessDTO;
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
