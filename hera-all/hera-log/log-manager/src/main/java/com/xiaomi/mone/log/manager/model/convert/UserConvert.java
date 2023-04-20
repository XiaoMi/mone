package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.manager.user.MoneUser;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConvert {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    @Mappings({
            @Mapping(target = "user", source = "account"),
            @Mapping(target = "displayName", source = "name"),
            @Mapping(target = "UID", source = "casUid")
    })
    MoneUser userAdapter(AuthUserVo userVo);

}
