package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.manager.model.cache.IDMDeptCache;
import com.xiaomi.mone.log.manager.model.dto.PermTreeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
@Mapper
public interface SpacePermTreeConvert {
    SpacePermTreeConvert INSTANCE = Mappers.getMapper(SpacePermTreeConvert.class);

    @Mappings({
            @Mapping(target = "id", source = "deptId"),
            @Mapping(target = "label", source = "deptName")
    })
    PermTreeDTO fromCache(IDMDeptCache cache);

}
