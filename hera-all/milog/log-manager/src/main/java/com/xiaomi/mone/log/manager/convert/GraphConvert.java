package com.xiaomi.mone.log.manager.convert;

import com.xiaomi.mone.log.manager.model.pojo.MilogAnalyseGraphDO;
import com.xiaomi.mone.log.manager.model.vo.CreateGraphCmd;
import com.xiaomi.mone.log.manager.model.vo.UpdateGraphCmd;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GraphConvert {
    GraphConvert INSTANCE = Mappers.getMapper(GraphConvert.class);

    MilogAnalyseGraphDO toDO(CreateGraphCmd cmd);

    MilogAnalyseGraphDO toDO(UpdateGraphCmd cmd);

}
