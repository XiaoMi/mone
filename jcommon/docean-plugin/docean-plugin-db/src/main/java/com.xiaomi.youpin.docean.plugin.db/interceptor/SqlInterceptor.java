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
