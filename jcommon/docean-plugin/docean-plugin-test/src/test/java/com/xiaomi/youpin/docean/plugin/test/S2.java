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

package com.xiaomi.youpin.docean.plugin.test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mi.youpin.TestService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.exception.DoceanException;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.docean.plugin.datasource.anno.Transactional;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import com.xiaomi.youpin.docean.plugin.http.Http;
import com.xiaomi.youpin.docean.plugin.log.Log;
import com.xiaomi.youpin.docean.plugin.sql.Db;
import com.xiaomi.youpin.docean.plugin.sql.Session;
import com.xiaomi.youpin.docean.plugin.test.mybatis.Test;
import com.xiaomi.youpin.docean.plugin.test.mybatis.TestMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
//import org.influxdb.dto.QueryResult;
//import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2020/6/26
 */
@Service
public class S2 {

    @Reference(interfaceClass = IS.class)
    private IS s;

    @Reference(interfaceClass = TestService.class, group = "$group", check = false)
    private TestService testService;

    @Resource
    private TestDao testDao;

    @Resource
    private TestRocketmq testRocketmq;

    @Resource
    private TinfluxDb tinfluxDb;

    @Value("$c")
    private String c;

    @Value("$b")
    private String b;

    @Value(value = "$k", defaultValue = "gogok")
    private String k;

    @Resource
    private Http http;

    @Resource(name="mione_test1")
    private Db db;

    @Resource
    private Log dlog;

    @Resource(name = "nutz_ds_2")
    private NutDao dao;


    @Resource(name = "mybatis_ds_2")
    private SqlSessionFactory sqlSessionFactory;

    public String hi() {
        return s.hi()
                + ":dubbo:" + testService.sum(11, 11)
                + ":mysql:" + testDao.get()
                + ":redis:" + testDao.key()
                + ":config:" + c
                + ":nacos config:" + b
                + ":http:" + http.get("http://www.baidu.com", Maps.newHashMap(), 1000).getCode()
                ;
    }
//    public QueryResult intert2Influxdb(){
//        tinfluxDb.doInsert();
//        QueryResult result = tinfluxDb.getList();
//        return result;
//    }

    public String db() {
        return new Gson().toJson(testDao.get());
    }

    public void rocketproduce() {
        testRocketmq.produce();
    }

    public void rocketconsume() {
        testRocketmq.consume();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String redis() {
        return new Gson().toJson(testDao.key());
    }

    public String dubbo() {
        return testService.sum(11, 22) + "";
    }


    public String sql() {
        Session session = db.openSession();
        try {
            Object res = session.query("select * from test1 where id=?", 7);
            session.commit();
            return res + "";
        } catch (Throwable ex) {
            session.rollback();
        } finally {
            session.close();
        }
        return "";
    }

    /**
     * 支持事务注解
     *
     * @return
     */
    @Transactional
    public String sql2() {
        Session session = db.openSession();
        Object res = session.update("insert into  test1 value(?,'[]',null)", 12);
        Object res2 = session.update("insert into  test1 value(?,'[]',null)", 13);
        return "sql2";
    }


    @com.xiaomi.youpin.docean.plugin.db.Transactional(type = "nutz")
    public String nutz() {
        dao.insert(new com.xiaomi.youpin.docean.plugin.test.bo.Test(0,"{}"));
        if (true) {
            throw new DoceanException();
        }
        dao.insert(new com.xiaomi.youpin.docean.plugin.test.bo.Test(0,"{}"));
        return "nutz";
    }


    /**
     * 支持mybatis事务
     *
     * @return
     */
    @com.xiaomi.youpin.docean.plugin.mybatis.Transactional(type = "mybatis")
    public String mybatis2() {
        SqlSession session = sqlSessionFactory.openSession();
        TestMapper m = session.getMapper(TestMapper.class);
        m.insert(new Test(15));
        m.insert(new Test(16));
        return "mybatis2";
    }

    public String mybatis() {
        SqlSession session = sqlSessionFactory.openSession();
        TestMapper m = session.getMapper(TestMapper.class);
        try {
            return m.list() + "";
        } finally {
            session.commit();
            session.close();
        }
    }

    public String testDLog() {
        dlog.info("","--------->info log");
        return "test log";
    }

    public String testK() {
        return this.k;
    }

}
