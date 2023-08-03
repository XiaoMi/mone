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
package com.xiaomi.mone.log.manager.service.bind;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;

import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description 读取数据库的方式判断
 * @date 2022/12/23 14:03
 */
@Processor(isDefault = true, order = 1000)
public class DataSourceLogTypeProcessor implements LogTypeProcessor {

    private static final Integer EXIST_STATUS = 1;

    private final MilogLogTemplateMapper milogLogTemplateMapper;

    public DataSourceLogTypeProcessor(MilogLogTemplateMapper milogLogTemplateMapper) {
        this.milogLogTemplateMapper = milogLogTemplateMapper;
    }

    @Override
    public boolean supportedConsume(Integer logTypeCode) {
        QueryWrapper<MilogLogTemplateDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", logTypeCode);
        MilogLogTemplateDO templateDO = milogLogTemplateMapper.selectOne(queryWrapper);
        if (null == templateDO) {
            throw new MilogManageException("log template not exist,logtypeType:" + logTypeCode);
        }
        return Objects.equals(EXIST_STATUS, templateDO.getSupportedConsume());
    }
}
