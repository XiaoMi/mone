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
package com.xiaomi.mone.log.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDTO;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDetailDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTemplateDO;

import java.io.IOException;
import java.util.List;

public interface LogTemplateService extends IService<MilogLogTemplateDO> {
    /**
     * 日志模板列表
     *
     * @return
     */
    Result<List<LogTemplateDTO>> getLogTemplateList(String area);

    /**
     * 获取日志模板
     *
     * @param logTemplateId
     * @return
     * @throws IOException
     */
    Result<LogTemplateDetailDTO> getLogTemplateById(long logTemplateId);
}
