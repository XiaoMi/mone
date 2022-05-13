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

import com.google.common.collect.Lists;
import com.xiaomi.mione.plugin.dao.mapper.TaskMapper;
import com.xiaomi.mione.plugin.dao.model.Task;
import com.xiaomi.mione.plugin.dao.model.TaskExample;
import com.xiaomi.mione.plugin.dao.model.TaskWithBLOBs;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.ClassPathResource;
import com.xiaomi.youpin.docean.common.Resource;
import com.xiaomi.youpin.docean.plugin.mybatis.SqlSessionFactoryBean;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.UpdateParam;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.QueryParam;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.QueryResult;
import com.xiaomi.youpin.docean.plugin.mybatis.interceptor.DoceanInterceptor;
import com.xiaomi.youpin.docean.plugin.mybatis.interceptor.InterceptorForQryAndUpdate;
import com.xiaomi.youpin.docean.plugin.mybatis.interceptor.InterceptorFunction;
import com.xiaomi.youpin.docean.plugin.test.common.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
@Slf4j
public class MybatisTest {


    @Test
    public void testMy() {
        Ioc.ins().init("com.xiaomi.youpin.docean");
    }


    @Test
    public void testMybatis() throws IOException, PropertyVetoException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{new DoceanInterceptor(), new InterceptorForQryAndUpdate(new InterceptorFunction() {
            @Override
            public QueryResult query(QueryParam param) {
                return null;
            }

            @Override
            public int update(UpdateParam param) {
                return 0;
            }
        })});
        sqlSessionFactoryBean.setDataSource(DbUtils.datasource());
        sqlSessionFactoryBean.setMapperLocations(new Resource[]{
                new ClassPathResource("mapper/test.xml"),
                new ClassPathResource("com/xiaomi/mione/plugin/dao/mapper/TaskMapper.xml"),
        });
        SqlSessionFactory factory = sqlSessionFactoryBean.buildSqlSessionFactory();

//        Collection<Class<?>> mappers = factory.getConfiguration().getMapperRegistry().getMappers();

        SqlSession session = factory.openSession();
        List<Object> res = session.selectList("list");
        System.out.println(res);

        List<Object> resList = session.selectList("query", Lists.newArrayList("1", "2"));
        System.out.println(resList);

        TestMapper tm = session.getMapper(TestMapper.class);
        List<com.xiaomi.youpin.docean.plugin.test.mybatis.Test> res2 = tm.list();
        tm.query(11, 22);
        System.out.println(res2);
        session.close();
    }


    @Test
    public void testMybatis2() throws IOException, PropertyVetoException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setPlugins(new Interceptor[]{new DoceanInterceptor(), new InterceptorForQryAndUpdate(new InterceptorFunction() {
//            @Override
//            public QueryResult query(QueryParam param) {
//                return null;
//            }
//
//            @Override
//            public int update(UpdateParam param) {
//                return 0;
//            }
//        })});
//        sqlSessionFactoryBean.setPlugins(new Interceptor[]{new DoceanInterceptor()});
        sqlSessionFactoryBean.setDataSource(DbUtils.datasource());
        sqlSessionFactoryBean.setMapperLocations(new Resource[]{
                new ClassPathResource("com/xiaomi/mione/plugin/dao/mapper/TaskMapper.xml"),
        });
        SqlSessionFactory factory = sqlSessionFactoryBean.buildSqlSessionFactory();

//        Collection<Class<?>> mappers = factory.getConfiguration().getMapperRegistry().getMappers();

        SqlSession session = factory.openSession();
        TaskMapper tm = session.getMapper(TaskMapper.class);
        TaskExample example = new TaskExample();
        example.createCriteria().andIdEqualTo(9);
        List<Task> list = tm.selectByExample(example);
        System.out.println(list.size() + ":" + list);
        session.close();
    }


    @Test
    public void testMybatisInsert() throws IOException, PropertyVetoException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

//        sqlSessionFactoryBean.setPlugins(new Interceptor[]{new DoceanInterceptor(), new InterceptorForQryAndUpdate(new InterceptorFunction() {
//            @Override
//            public QueryResult query(QueryParam param) {
//                return null;
//            }
//
//            @Override
//            public int update(UpdateParam param) {
//                return 0;
//            }
//        })});


        sqlSessionFactoryBean.setDataSource(DbUtils.datasource());
        sqlSessionFactoryBean.setMapperLocations(new Resource[]{
                new ClassPathResource("com/xiaomi/mione/plugin/dao/mapper/TaskMapper.xml"),
        });
        SqlSessionFactory factory = sqlSessionFactoryBean.buildSqlSessionFactory();
        SqlSession session = factory.openSession();
        TaskMapper tm = session.getMapper(TaskMapper.class);
        TaskExample example = new TaskExample();
        example.createCriteria().andIdEqualTo(9);

        List<Task> v = tm.selectByExample(example);
        log.info("{}", v);

        TaskWithBLOBs t = new TaskWithBLOBs();
        t.setId(9);
        t.setBid("1");
        t.setParams("params");
//        t.setName(IntStream.range(0,100).mapToObj(i->"name").collect(Collectors.joining()));
        t.setName("name");
        int n = tm.insert(t);
        log.info("{}", n);
        session.commit();
        session.close();
    }


    @Test
    public void testMybatisDelete() throws IOException, PropertyVetoException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{new DoceanInterceptor(), new InterceptorForQryAndUpdate(new InterceptorFunction() {
            @Override
            public QueryResult query(QueryParam param) {
                return null;
            }

            @Override
            public int update(UpdateParam param) {
                return 0;
            }
        })});
        sqlSessionFactoryBean.setDataSource(DbUtils.datasource());
        sqlSessionFactoryBean.setMapperLocations(new Resource[]{
                new ClassPathResource("com/xiaomi/mione/plugin/dao/mapper/TaskMapper.xml"),
        });
        SqlSessionFactory factory = sqlSessionFactoryBean.buildSqlSessionFactory();
        SqlSession session = factory.openSession();
        TaskMapper tm = session.getMapper(TaskMapper.class);
        TaskExample example = new TaskExample();
        example.createCriteria().andIdEqualTo(9);
        tm.deleteByPrimaryKey(9);
        session.close();
    }

    @Test
    public void testMybatisUpdate() throws IOException, PropertyVetoException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{new DoceanInterceptor(), new InterceptorForQryAndUpdate(new InterceptorFunction() {
            @Override
            public QueryResult query(QueryParam param) {
                return null;
            }

            @Override
            public int update(UpdateParam param) {
                return 0;
            }
        })});
        sqlSessionFactoryBean.setDataSource(DbUtils.datasource());
        sqlSessionFactoryBean.setMapperLocations(new Resource[]{
                new ClassPathResource("com/xiaomi/mione/plugin/dao/mapper/TaskMapper.xml"),
        });
        SqlSessionFactory factory = sqlSessionFactoryBean.buildSqlSessionFactory();
        SqlSession session = factory.openSession();
        TaskMapper tm = session.getMapper(TaskMapper.class);
        TaskExample example = new TaskExample();
        example.createCriteria().andIdEqualTo(9);
        Task t = new Task();
        t.setName("zzy");
        tm.updateByExample(t, example);
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
