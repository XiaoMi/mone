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
            //SqlMapConfig.xml读给输入流，使用mybitis的Resources类下的getResourceAsStream实现
            InputStream inputStream = null;
            inputStream = Resources.getResourceAsStream(resource);
            //创建Mybitis的SqlSessionFactory工厂类
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