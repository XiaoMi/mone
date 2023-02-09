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

package com.xiaomi.youpin.docean.plugin.test.sql;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;
import com.xiaomi.youpin.docean.plugin.sql.*;
import com.xiaomi.youpin.docean.plugin.test.common.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
public class SqlTest {


    @Test
    public void testDbForQuery() throws PropertyVetoException {
        ComboPooledDataSource dataSource = DbUtils.datasource("com.xiaomi.youpin.docean.plugin.db.interceptor.SqlInterceptor");
        Db db = new Db(dataSource, new JdbcTransaction(dataSource, null, true));
        IntStream.range(0, 1).forEach(it -> {
            List<Map<String, ColumnRecord>> list = db.openSession().query("select * from test1 where id = ?", 8);
            System.out.println(list);
        });
    }


    /**
     * 获取所有表名称
     * @throws PropertyVetoException
     */
    @Test
    public void testTables() throws PropertyVetoException {
        ComboPooledDataSource dataSource = DbUtils.datasource("");
        Db db = new Db(dataSource, new JdbcTransaction(dataSource, null, true));
        db.tables("gateway_web").forEach(System.out::println);
    }


    /**
     * 获取表结构
     * @throws PropertyVetoException
     */
    @Test
    public void testDesc() throws PropertyVetoException {
        ComboPooledDataSource dataSource = DbUtils.datasource("");
        Db db = new Db(dataSource, new JdbcTransaction(dataSource, null, true));
        db.desc("gateway_web", "account").forEach(System.out::println);
    }


    @Test
    public void testQuery() throws PropertyVetoException {
        ComboPooledDataSource dataSource = DbUtils.datasource();
        Db db = new Db(dataSource, new JdbcTransaction(dataSource, null, true));
        List<Map<String, ColumnRecord>> list = db.openSession().query("select * from test where id = ?", 126);
        System.out.println(list);
    }


    @Test
    public void testDbForUpdate() throws PropertyVetoException {
        ComboPooledDataSource dataSource = DbUtils.datasource();
        Db db = new Db(dataSource);
        int n = db.openSession().update("update test1 set id = 7 where id = ?", 10);
        System.out.println(n);
    }


    @Test
    public void testInsert() throws PropertyVetoException {
        ComboPooledDataSource dataSource = DbUtils.datasource();
        Db db = new Db(dataSource);
        Session s = db.openSession();
        s.update("insert into test values(3,null)");
    }


    @Test
    public void testDynamic() {
        Ioc.ins().init("com.xiaomi");
        SqlPlugin sqlPlugin = Ioc.ins().getBean(SqlPlugin.class);
        System.out.println(sqlPlugin);
        DatasourceConfig config = DbUtils.datasourceConfig();
        sqlPlugin.add(config);
        Db db = Ioc.ins().getBean("dao:dynamic");
        List<Map<String, ColumnRecord>> list = db.openSession().query("select * from test1 where id = ?", 8);
        System.out.println(list.size());
        sqlPlugin.remove(config);
        try {
            list = db.openSession().query("select * from test1 where id = ?", 8);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        sqlPlugin.add(config);
        try {
            db = Ioc.ins().getBean("dao:dynamic");
            list = db.openSession().query("select * from test1 where id = ?", 8);
            System.out.println(list.size());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }


}
