package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.manager.model.dto.LogTemplateDetailDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
@Mapper
public interface MilogLongTemplateDetailConvert {
    MilogLongTemplateDetailConvert INSTANCE = Mappers.getMapper(MilogLongTemplateDetailConvert.class);

    @Mappings({
            @Mapping(target = "templateName", source = "templateDO.templateName"),
            @Mapping(target = "propertiesKey", source = "detailDO.propertiesKey"),
            @Mapping(target = "propertiesType", source = "detailDO.propertiesType")
    })
    LogTemplateDetailDTO fromDO(MilogLogTemplateDO templateDO, MilogLogTemplateDetailDO detailDO);
}
