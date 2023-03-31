package com.xiaomi.mone.log.manager.convert;

import com.xiaomi.mone.log.manager.model.bo.MilogLogtailParam;
import com.xiaomi.mone.log.manager.model.dto.MilogTailDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MilogLogtailConvert {
    MilogLogtailConvert INSTANCE = Mappers.getMapper(MilogLogtailConvert.class);

    MilogTailDTO fromDO(MilogLogTailDo milogLogtailDo);

    MilogLogTailDo fromDo(MilogLogtailParam milogLogtailParam);
}
