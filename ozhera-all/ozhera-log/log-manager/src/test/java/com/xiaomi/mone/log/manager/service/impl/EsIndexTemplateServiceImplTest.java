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

import com.xiaomi.mone.log.manager.domain.EsIndexTemplate;
import com.xiaomi.mone.log.manager.model.vo.CreateIndexTemplatePropertyCommand;
import com.xiaomi.mone.log.manager.model.vo.UpdateIndexTemplateCommand;
import com.xiaomi.youpin.docean.Ioc;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EsIndexTemplateServiceImplTest {

    private void testUpdateIndexTemplate() {
        Ioc.ins().init("com.xiaomi");
        EsIndexTemplateServiceImpl esIndexTemplateService = Ioc.ins().getBean(EsIndexTemplateServiceImpl.class);
        UpdateIndexTemplateCommand command = new UpdateIndexTemplateCommand();
        // Set up
        command.setIndexTemplateName("auto_create_index");
        command.setIndexShards(3);
        command.setIndexReplicas(0);
        command.setLifecycle("7Del");

        // attribute
        List<CreateIndexTemplatePropertyCommand> propertyList = new ArrayList<>();
        CreateIndexTemplatePropertyCommand message = new CreateIndexTemplatePropertyCommand();
        message.setName("message");
        message.setType("text");
        propertyList.add(message);
        CreateIndexTemplatePropertyCommand name = new CreateIndexTemplatePropertyCommand();
        name.setName("name");
        name.setType("text");
        propertyList.add(name);
        CreateIndexTemplatePropertyCommand timestamp = new CreateIndexTemplatePropertyCommand();
        timestamp.setName("timestamp");
        timestamp.setType("date");
        propertyList.add(timestamp);

        command.setPropertyList(propertyList);

        // Perform the creation
        try {
            System.out.println("====================" + esIndexTemplateService.updateIndexTemplate(command));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateIndex() {
        try {
            Ioc.ins().init("com.xiaomi");
            EsIndexTemplateServiceImpl esIndexTemplateService = Ioc.ins().getBean(EsIndexTemplateServiceImpl.class);
            String templateName = "";
            System.out.println("============================" + esIndexTemplateService.createIndex(templateName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void logstoreEsIndexRef2() {
        Ioc.ins().init("com.xiaomi");
        EsIndexTemplate indexTemplate = Ioc.ins().getBean(EsIndexTemplate.class);
        Assert.assertEquals("zgq_common_milog_staging_app_private_1", indexTemplate.getClusterTypeIndex(1l, 1));
    }

    @Test
    public void getRegionTypeIndex() {
        Ioc.ins().init("com.xiaomi");
        EsIndexTemplate indexTemplate = Ioc.ins().getBean(EsIndexTemplate.class);
        Assert.assertEquals("", indexTemplate.getRegionTypeIndex(null, 1));
        Assert.assertEquals("", indexTemplate.getRegionTypeIndex("c3", null));
        Assert.assertEquals("", indexTemplate.getRegionTypeIndex(null, null));
        Assert.assertEquals("zgq_common_milog_staging_app_c3_1", indexTemplate.getRegionTypeIndex("c3", 1));
    }

    @Test
    public void getAreaTypeIndex() {
        Ioc.ins().init("com.xiaomi");
        EsIndexTemplate indexTemplate = Ioc.ins().getBean(EsIndexTemplate.class);
        Assert.assertEquals("", indexTemplate.getAreaTypeIndex(null, 1));
        Assert.assertEquals("", indexTemplate.getAreaTypeIndex("cn", null));
        Assert.assertEquals("", indexTemplate.getAreaTypeIndex(null, null));
        Assert.assertEquals("zgq_common_milog_staging_app_amstega_1", indexTemplate.getAreaTypeIndex("ams", 1));
    }
}