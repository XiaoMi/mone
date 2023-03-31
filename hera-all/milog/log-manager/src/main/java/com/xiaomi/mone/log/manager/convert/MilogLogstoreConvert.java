package com.xiaomi.mone.log.manager.convert;

import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.vo.LogStoreParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MilogLogstoreConvert {
    MilogLogstoreConvert INSTANCE = Mappers.getMapper(MilogLogstoreConvert.class);

    MilogLogStoreDO fromCommad(LogStoreParam command);
}
