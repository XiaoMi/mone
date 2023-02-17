package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.manager.model.bo.CreateOrUpdateSpaceCmd;
import com.xiaomi.mone.log.manager.model.dto.MilogSpaceDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.LogSpaceDO;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SpaceConvert {
    SpaceConvert INSTANCE = Mappers.getMapper(SpaceConvert.class);

    MilogSpaceDTO fromDO(LogSpaceDO milogSpace);

    List<MilogSpaceDTO> fromDOList(List<LogSpaceDO> milogSpace);

    PageInfo<MilogSpaceDTO> fromTpcPage(PageDataVo<NodeVo> tpcRes);

    @Mappings({
        @Mapping(target = "id", source = "outId"),
        @Mapping(target = "spaceName", source = "nodeName"),
        @Mapping(target = "ctime", source = "createTime"),
        @Mapping(target = "utime", source = "updateTime"),
        @Mapping(target = "creator", source = "createrAcc"),
        @Mapping(target = "description", source = "desc"),
        @Mapping(target = "tpcNodeId", source = "id"),
    })
    MilogSpaceDTO fromTpc(NodeVo tpcNode);

    LogSpaceDO toDO(CreateOrUpdateSpaceCmd cmd);
}
