package com.xiaomi.mone.log.manager.convert;

import com.xiaomi.mone.log.manager.model.dto.DashboardDTO;
import com.xiaomi.mone.log.manager.model.dto.DashboardGraphDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogAnalyseDashboardDO;
import com.xiaomi.mone.log.manager.model.vo.CreateDashboardCmd;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DashboardConvert {
    DashboardConvert INSTANCE = Mappers.getMapper(DashboardConvert.class);

    @Mappings({
            @Mapping(source = "dashboardDO.name", target = "dashboardName"),
            @Mapping(source = "dashboardDO.id", target = "dashboardId")
    })
    DashboardDTO fromDO(MilogAnalyseDashboardDO dashboardDO, List<DashboardGraphDTO> graphList);

    MilogAnalyseDashboardDO toDO(CreateDashboardCmd cmd);
}
