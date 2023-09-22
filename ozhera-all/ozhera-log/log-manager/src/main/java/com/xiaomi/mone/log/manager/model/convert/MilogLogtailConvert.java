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

import com.xiaomi.mone.log.manager.model.bo.LogTailParam;
import com.xiaomi.mone.log.manager.model.dto.LogTailDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MilogLogtailConvert {
    MilogLogtailConvert INSTANCE = Mappers.getMapper(MilogLogtailConvert.class);

    LogTailDTO fromDO(MilogLogTailDo milogLogtailDo);

    MilogLogTailDo fromDo(LogTailParam logTailParam);
}
