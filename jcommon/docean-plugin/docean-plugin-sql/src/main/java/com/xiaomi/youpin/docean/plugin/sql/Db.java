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

package com.xiaomi.youpin.docean.plugin.sql;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * 数据库操作
 */
public class Db {

    private DataSource dataSource;

    private JdbcTransaction transaction;

    public Db(DataSource ds, JdbcTransaction jdbcTransaction) {
        this.dataSource = ds;
        this.transaction = jdbcTransaction;
    }

    public Db(DataSource ds) {
        this.dataSource = ds;
        this.transaction = new JdbcTransaction(ds, null, true);
    }

    public Db() {
    }


    public Session openSession() {
        if (TransactionalContext.getContext().get() != null) {
            return new Session(TransactionalContext.getContext().get(), dataSource);
        }
        return new Session(transaction, dataSource);
    }

    public List<Map<String, ColumnRecord>> query(String sql, String... params) {
        Session session = openSession();
        return session.query(sql, params);
    }


    public int update(String sql, String... params) {
        Session session = openSession();
        int n = session.update(sql, params);
        session.commit();
        return n;
    }

    public List<String> tables(String schemaName) {
        Session session = openSession();
        return session.tables(schemaName);
    }


    public List<Map<String, ColumnRecord>> desc(String schemaName, String tableName) {
        Session session = openSession();
        return session.desc(schemaName, tableName);
    }


}
