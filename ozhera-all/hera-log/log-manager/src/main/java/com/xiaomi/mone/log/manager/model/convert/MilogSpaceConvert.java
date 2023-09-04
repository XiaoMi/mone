/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.model.convert;

import com.xiaomi.mone.log.manager.model.dto.MilogSpaceDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MilogSpaceConvert {
    MilogSpaceConvert INSTANCE = Mappers.getMapper(MilogSpaceConvert.class);

    MilogSpaceDTO fromDO(MilogSpaceDO milogSpace);

    List<MilogSpaceDTO> fromDOList(List<MilogSpaceDO> milogSpace);

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
}
