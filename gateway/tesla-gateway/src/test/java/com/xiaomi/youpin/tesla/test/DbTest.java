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

package com.xiaomi.youpin.tesla.test;

import org.junit.Test;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.dao.sql.Sql;

import java.util.List;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/15 14:41
 */
public class DbTest {

    private Dao dao() throws ClassNotFoundException {
        SimpleDataSource ds = new SimpleDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mone?characterEncoding=utf8&useSSL=false");
        ds.setUsername("mone_x");
        ds.setPassword("ALaMiZw4E6EKlrQn9WhcNc20fp6LQXee");
        Dao dao = new NutDao(ds);
        return dao;
    }


    @Test
    public void testInit() throws ClassNotFoundException {
        Dao dao = dao();
//        dao.create(ScriptTable.class, true);
    }


    @Test
    public void testQuery() throws ClassNotFoundException {
        Dao dao = dao();
        Sql sql = Sqls.create("select * from task where id=@id limit 10");
        sql.params().set("id", 10);
        sql.setCallback(Sqls.callback.integer());
        List<Map> list = dao.execute(sql).getList(Map.class);
        System.out.println(list.size());
    }


    @Test
    public void testUpdate() throws ClassNotFoundException {
        Dao dao = dao();
        Sql sql = Sqls.create("delete from test where id=@id");
        sql.params().set("id", 126);
        sql.setCallback(Sqls.callback.integer());
        dao.execute(sql);
        System.out.println(sql.getUpdateCount());
    }
}
