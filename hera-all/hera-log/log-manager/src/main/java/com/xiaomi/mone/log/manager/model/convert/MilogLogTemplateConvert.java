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

import com.xiaomi.mone.log.manager.model.dto.LogTemplateDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MilogLogTemplateConvert {
    MilogLogTemplateConvert INSTANCE = Mappers.getMapper(MilogLogTemplateConvert.class);

    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "templateName")
    })
    LogTemplateDTO fromDO(MilogLogTemplateDO milogLogTemplateDO);

    List<LogTemplateDTO> fromDOList(List<MilogLogTemplateDO> milogLogTemplateDOList);

}
