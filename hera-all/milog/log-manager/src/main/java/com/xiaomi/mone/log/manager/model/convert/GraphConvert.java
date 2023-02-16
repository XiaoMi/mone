package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.manager.model.pojo.LogAnalyseGraphDO;
import com.xiaomi.mone.log.manager.model.vo.CreateGraphCmd;
import com.xiaomi.mone.log.manager.model.vo.UpdateGraphCmd;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GraphConvert {
    GraphConvert INSTANCE = Mappers.getMapper(GraphConvert.class);

    LogAnalyseGraphDO toDO(CreateGraphCmd cmd);

    LogAnalyseGraphDO toDO(UpdateGraphCmd cmd);

}
