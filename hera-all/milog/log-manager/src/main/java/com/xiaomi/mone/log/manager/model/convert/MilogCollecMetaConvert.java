package com.xiaomi.mone.log.manager.model.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MilogCollecMetaConvert {
    MilogCollecMetaConvert INSTANCE = Mappers.getMapper(MilogCollecMetaConvert.class);


}
