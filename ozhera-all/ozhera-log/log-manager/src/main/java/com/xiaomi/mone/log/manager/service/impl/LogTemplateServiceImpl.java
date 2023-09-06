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
package com.xiaomi.mone.log.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateDetailMapper;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.mone.log.manager.model.convert.MilogLogTemplateConvert;
import com.xiaomi.mone.log.manager.model.convert.MilogLongTemplateDetailConvert;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDTO;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDetailDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDetailDO;
import com.xiaomi.mone.log.manager.service.LogTemplateService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class LogTemplateServiceImpl extends ServiceImpl<MilogLogTemplateMapper, MilogLogTemplateDO> implements LogTemplateService {

    @Resource
    private MilogLogTemplateMapper milogLogTemplateMapper;

    @Resource
    private MilogLogTemplateDetailMapper templateDetailMapper;

    /**
     * A list of log templates
     *
     * @return
     */
    @Override
    public Result<List<LogTemplateDTO>> getLogTemplateList(String area) {
        List<MilogLogTemplateDO> logTemplateDOList = milogLogTemplateMapper.selectSupportedTemplate(area);
        List<LogTemplateDTO> logTemplateDTOList = MilogLogTemplateConvert.INSTANCE.fromDOList(logTemplateDOList);
        assembleLogTemplateDetail(logTemplateDTOList);
        return Result.success(logTemplateDTOList);
    }

    private void assembleLogTemplateDetail(List<LogTemplateDTO> logTemplateDTOList) {
        if (CollectionUtils.isNotEmpty(logTemplateDTOList)) {
            logTemplateDTOList.forEach(logTemplateDTO -> {
                logTemplateDTO.setLogTemplateDetailDTOList(getLogTemplateById(logTemplateDTO.getValue()).getData());
                LogTypeEnum logTypeEnum = LogTypeEnum.type2enum(logTemplateDTO.getType());
                logTemplateDTO.setDescribe(null != logTypeEnum ? logTypeEnum.getDescribe() : StringUtils.EMPTY);
            });
        }
    }


    /**
     * Get the log template
     *
     * @param logTemplateId
     * @return
     */
    @Override
    public Result<LogTemplateDetailDTO> getLogTemplateById(long logTemplateId) {
        MilogLogTemplateDO logTemplate = milogLogTemplateMapper.selectById(logTemplateId);
        MilogLogTemplateDetailDO tmplateDetail = templateDetailMapper.getByTemplateId(logTemplateId);
        if (tmplateDetail == null) {
            return Result.success(null);
        }
        LogTemplateDetailDTO dto = MilogLongTemplateDetailConvert.INSTANCE.fromDO(logTemplate, tmplateDetail);
        return Result.success(dto);
    }

}
