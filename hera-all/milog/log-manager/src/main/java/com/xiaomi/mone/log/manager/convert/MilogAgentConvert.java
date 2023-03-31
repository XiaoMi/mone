package com.xiaomi.mone.log.manager.convert;

import com.xiaomi.mone.log.manager.model.dto.MilogAgentDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogAgentDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogComputerRoomDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogProcessDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MilogAgentConvert {
    MilogAgentConvert INSTANCE = Mappers.getMapper(MilogAgentConvert.class);

    @Mappings({
            @Mapping(source = "milogComputerRoomDO.roomName", target = "computerRoomName"),
            @Mapping(source = "milogAgentDO.id", target = "id"),
            @Mapping(source = "logProcessDOList", target = "processList"),
    })
    MilogAgentDTO fromDO(MilogAgentDO milogAgentDO, MilogComputerRoomDO milogComputerRoomDO, List<MilogLogProcessDO> logProcessDOList);

}
