package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.vo.CreateOrUpdateLogStoreCmd;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LogStoreConvert {

    LogStoreConvert INSTANCE = Mappers.getMapper(LogStoreConvert.class);

    MilogLogStoreDO fromCmd(CreateOrUpdateLogStoreCmd cmd);
}
