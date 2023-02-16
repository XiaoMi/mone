package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.manager.model.pojo.LogDashboardGraphRefDO;
import com.xiaomi.mone.log.manager.model.vo.DGRefCmd;
import com.xiaomi.mone.log.manager.model.vo.DGRefUpdateCmd;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DGRefConvert {
    DGRefConvert INSTANCE = Mappers.getMapper(DGRefConvert.class);

    LogDashboardGraphRefDO toDo(DGRefCmd cmd);

    @Mappings({
            @Mapping(target = "privateName", source = "graphPrivateName")
    })
    LogDashboardGraphRefDO toDo(DGRefUpdateCmd.DGRefDetailUpdateCmd cmd);
}
