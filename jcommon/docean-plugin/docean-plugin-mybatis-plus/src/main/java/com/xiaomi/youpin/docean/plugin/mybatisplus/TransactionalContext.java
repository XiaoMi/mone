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

package com.xiaomi.youpin.docean.plugin.mybatisplus;

import org.apache.ibatis.session.SqlSession;
import org.springframework.core.NamedThreadLocal;

import java.sql.Connection;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
public class TransactionalContext {

    private static ThreadLocal<TransactionalContext> context = new ThreadLocal<TransactionalContext>() {
        @Override
        protected TransactionalContext initialValue() {
            return new TransactionalContext();
        }
    };

    private MybatisTransaction jdbcTransaction;


    public void set(MybatisTransaction transaction) {
        this.jdbcTransaction = transaction;
    }

    public MybatisTransaction get() {
        return this.jdbcTransaction;
    }


    public static TransactionalContext getContext() {
        return context.get();
    }

    public void close() {
        context.remove();
        transactionSqlSession.remove();
        connection.remove();
        transactionActive.remove();
    }

    private static ThreadLocal<SqlSession> transactionSqlSession = new NamedThreadLocal<>("transactionSqlSession");

    private static ThreadLocal<Connection> connection = new NamedThreadLocal<>("connection");

    private static ThreadLocal<Boolean> transactionActive = new NamedThreadLocal<>("transactionActive");

    public static SqlSession getSqlSession() {
        return transactionSqlSession.get();
    }

    public static void setSqlSession(SqlSession sqlSession) {
        transactionSqlSession.set(sqlSession);
    }

    public static Connection getConnection() {
        return connection.get();
    }

    public static void setConnection(Connection conn) {
        connection.set(conn);
    }

    public static Boolean getTransactionActive() {
        return transactionActive.get();
    }

    public static void setTransactionActive(Boolean f) {
        transactionActive.set(f);
    }

}
