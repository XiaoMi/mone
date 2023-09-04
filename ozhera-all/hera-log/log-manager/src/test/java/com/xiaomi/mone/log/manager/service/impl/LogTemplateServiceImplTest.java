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

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDTO;
import com.xiaomi.mone.log.manager.model.dto.LogTemplateDetailDTO;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LogTemplateServiceImplTest {
    private LogTemplateServiceImpl logTemplateService;

    @Before
    public void initFiled() {
        Ioc.ins().init("com.xiaomi");
        logTemplateService = Ioc.ins().getBean(LogTemplateServiceImpl.class);
    }

    @Test
    public void getLogTemplateList() {
        Result<List<LogTemplateDTO>> logTemplateList = logTemplateService.getLogTemplateList("cn");
        System.out.println(logTemplateList.getData());
    }

    @Test
    public void getLogTemplateById() {
        Result<LogTemplateDetailDTO> logtemplate = logTemplateService.getLogTemplateById(84);
        System.out.println(logtemplate);
    }

}