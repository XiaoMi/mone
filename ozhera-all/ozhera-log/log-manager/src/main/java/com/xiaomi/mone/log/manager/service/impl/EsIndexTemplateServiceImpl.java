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

import com.xiaomi.mone.log.manager.domain.EsCluster;
import com.xiaomi.mone.log.manager.domain.EsIndexTemplate;
import com.xiaomi.mone.log.manager.domain.LogTemplate;
import com.xiaomi.mone.log.manager.model.vo.UpdateIndexTemplateCommand;
import com.xiaomi.mone.log.manager.service.EsIndexTemplateService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
public class EsIndexTemplateServiceImpl implements EsIndexTemplateService {
    @Resource
    private EsCluster esCluster;

    @Resource
    private EsIndexTemplate esIndexTemplate;

    @Resource
    private LogTemplate logTemplate;

    /**
     * Update the index template
     *
     * @param command
     * @return
     */
    @Override
    public boolean updateIndexTemplate(UpdateIndexTemplateCommand command) throws IOException {
        return false;
    }

    /**
     * Create an index
     *
     * @param templateName
     * @return
     */
    @Override
    public boolean createIndex(String templateName) throws IOException {
        return esIndexTemplate.createIndex(templateName);
    }

}
