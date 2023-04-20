package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.manager.model.dto.LogTemplateDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MilogLogTemplateConvert {
    MilogLogTemplateConvert INSTANCE = Mappers.getMapper(MilogLogTemplateConvert.class);

    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "templateName")
    })
    LogTemplateDTO fromDO(MilogLogTemplateDO milogLogTemplateDO);

    List<LogTemplateDTO> fromDOList(List<MilogLogTemplateDO> milogLogTemplateDOList);

}
