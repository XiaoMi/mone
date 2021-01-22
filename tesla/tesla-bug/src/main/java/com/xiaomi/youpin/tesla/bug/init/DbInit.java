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

package com.xiaomi.youpin.tesla.bug.init;

import com.xiaomi.youpin.tesla.bug.domain.Record;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;

/**
 * @author goodjava@qq.com
 */
public class DbInit {

    public static void main(String... args) throws ClassNotFoundException {
        Dao dao = dao();
//        dao.create(Record.class, true);
    }


    private static Dao dao() throws ClassNotFoundException {
        SimpleDataSource ds = new SimpleDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setJdbcUrl("JdbcUrl");
        ds.setUsername("shop_x");
        ds.setPassword("");
        Dao dao = new NutDao(ds);
        return dao;
    }
}
