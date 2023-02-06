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

package com.xiaomi.youpin.docean.plugin.db.interceptor;

import com.mysql.jdbc.*;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Properties;

/**
 * @author goodjava@qq.com
 * @date 2/7/21
 * <p>
 * 拦截sql执行
 */
@Slf4j
public class SqlInterceptor implements StatementInterceptor {
    @Override
    public void init(Connection connection, Properties properties) throws SQLException {

    }

    /**
     * 可以修改sql语句
     * @param sql
     * @param statement
     * @param connection
     * @return
     * @throws SQLException
     */
    @Override
    public ResultSetInternalMethods preProcess(String sql, Statement statement, Connection connection) throws SQLException {
        if (statement instanceof PreparedStatement) {
            PreparedStatement st = (PreparedStatement) statement;
            String rsql = st.asSql();
            log.info("sql:{}", rsql);
        }
        return null;
    }

    @Override
    public ResultSetInternalMethods postProcess(String sql, Statement statement, ResultSetInternalMethods resultSetInternalMethods, Connection connection) throws SQLException {
        return resultSetInternalMethods;
    }

    @Override
    public boolean executeTopLevelOnly() {
        return true;
    }

    @Override
    public void destroy() {

    }
}
