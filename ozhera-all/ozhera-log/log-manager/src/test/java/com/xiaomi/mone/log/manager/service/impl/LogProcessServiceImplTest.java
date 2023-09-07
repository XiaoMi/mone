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

import com.xiaomi.mone.log.manager.model.pojo.MilogLogProcessDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogProcessDOMybatis;
import com.xiaomi.youpin.docean.Ioc;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class LogProcessServiceImplTest {

    @Test
    public void testGet() {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = null;
            inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlsessionfactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession sqlsession = sqlsessionfactory.openSession();
            MilogLogProcessDO processDO = sqlsession.selectOne("com.xiaomi.mone.log.manager.mapper.MilogLogProcessMapper.getById", 1l);
            System.out.println(processDO);
            sqlsession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getById() throws IOException {
        Ioc.ins().init("com.xiaomi");
        LogProcessServiceImpl logProcessService = Ioc.ins().getBean(LogProcessServiceImpl.class);
        MilogLogProcessDOMybatis byId = logProcessService.getByIdFramework(1l);
        System.out.println(byId);
    }

    @Test
    public void updateLogProcess() {
    }
}