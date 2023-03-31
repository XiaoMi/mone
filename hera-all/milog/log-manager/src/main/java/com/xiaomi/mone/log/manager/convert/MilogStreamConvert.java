package com.xiaomi.mone.log.manager.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MilogStreamConvert {
    MilogStreamConvert INSTANCE = Mappers.getMapper(MilogStreamConvert.class);

}
