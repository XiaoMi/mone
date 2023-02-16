package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.manager.model.dto.GraphTypeDTO;
import com.xiaomi.mone.log.manager.model.pojo.LogAnalyseGraphTypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GraphTypeConvert {

    GraphTypeConvert INSTANCE = Mappers.getMapper(GraphTypeConvert.class);
    @Mappings({
            @Mapping(target = "typeCode", source = "type")
    })
    GraphTypeDTO toDTO(LogAnalyseGraphTypeDO DO);

    List<GraphTypeDTO> toDTOList(List<LogAnalyseGraphTypeDO> DOList);

}
