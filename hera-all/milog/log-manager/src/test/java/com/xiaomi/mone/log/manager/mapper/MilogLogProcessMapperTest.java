package com.xiaomi.mone.log.manager.mapper;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogNumAlertDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogProcessDOMybatis;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class MilogLogProcessMapperTest {

    @Test
    public void getById() throws IOException {
        String resource = "mybatis-config.xml";
        //SqlMapConfig.xml读给输入流，使用mybitis的Resources类下的getResourceAsStream实现
        InputStream inputStream = null;
        inputStream = Resources.getResourceAsStream(resource);
        //创建Mybitis的SqlSessionFactory工厂类
        SqlSessionFactory sqlsessionfactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlsession = sqlsessionfactory.openSession();
//        MilogLogProcessMapper mapper = sqlsession.getMapper(MilogLogProcessMapper.class);
//        MilogLogProcessDO byId = mapper.selectById(1l);
//        System.out.println(byId);
    }

    @Test
    public void selectById() {
        Ioc.ins().init("com.xiaomi");
        MilogLogProcessMapper mapper = Ioc.ins().getBean(MilogLogProcessMapper.class);
        MilogLogProcessDOMybatis process = mapper.selectById(1l);
        System.out.println(process);
    }

    @Test
    public void testSql() {
        Ioc.ins().init("com.xiaomi");
        MilogLogTailDao milogLogTailDao = Ioc.ins().getBean(MilogLogTailDao.class);
        Long spaceId = 6L;
        List<Long> storeIds = Lists.newArrayList(51L, 17L, 33L);
        Long minCountStoreId = milogLogTailDao.queryMinTailCountStoreId(spaceId, storeIds);
        log.info("result:{}", minCountStoreId);
    }

}