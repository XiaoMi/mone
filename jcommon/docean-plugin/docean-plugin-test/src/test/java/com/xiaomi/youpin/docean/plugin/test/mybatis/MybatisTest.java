/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.plugin.test.mybatis;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.xiaomi.youpin.docean.common.ClassPathResource;
import com.xiaomi.youpin.docean.common.Resource;
import com.xiaomi.youpin.docean.plugin.mybatis.SqlSessionFactoryBean;
import com.xiaomi.youpin.docean.plugin.test.common.DbUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
public class MybatisTest {


    @Test
    public void testMybatis() throws IOException, PropertyVetoException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(DbUtils.datasource());
        sqlSessionFactoryBean.setMapperLocations(new Resource[]{new ClassPathResource("mapper/test.xml")});
        SqlSessionFactory factory = sqlSessionFactoryBean.buildSqlSessionFactory();

        Collection<Class<?>> mappers = factory.getConfiguration().getMapperRegistry().getMappers();

        SqlSession session = factory.openSession();
        List<Object> res = session.selectList("list");
        System.out.println(res);

        TestMapper tm = session.getMapper(TestMapper.class);
        List<com.xiaomi.youpin.docean.plugin.test.mybatis.Test> res2 = tm.list();
        System.out.println(res2);
        session.close();
    }


    @Test
    public void testInsert() throws PropertyVetoException, IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(DbUtils.datasource());
        sqlSessionFactoryBean.setMapperLocations(new Resource[]{new ClassPathResource("mapper/test.xml")});
        SqlSessionFactory factory = sqlSessionFactoryBean.buildSqlSessionFactory();

        SqlSession session = factory.openSession();
        TestMapper tm = session.getMapper(TestMapper.class);

        tm.insert(new com.xiaomi.youpin.docean.plugin.test.mybatis.Test());
        session.commit();
        session.close();
    }
}
