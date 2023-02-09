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

package com.xiaomi.youpin.docean.plugin.mybatis;

import com.xiaomi.youpin.docean.plugin.mybatis.transaction.Xid;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;

import java.sql.Connection;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2020/7/7
 */
public class MybatisSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    private SqlSessionFactory sqlSessionFactory;

    public MybatisSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.configuration = this.sqlSessionFactory.getConfiguration();
    }

    @Override
    public SqlSession openSession() {
        return openSession(false);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        try {
            final Environment environment = configuration.getEnvironment();
            Transaction tx = getTransaction(environment, autoCommit);
            final Executor executor = configuration.newExecutor(tx, configuration.getDefaultExecutorType());
            return new DefaultSqlSession(configuration, executor, autoCommit);
        } finally {
            ErrorContext.instance().reset();
        }
    }


    private Transaction getTransaction(Environment environment, boolean autoCommit) {
        //sidecar传递过来的事务(跨机器夸线程的事务)
        Xid xid = SidecarTransactionContext.getXidLocal();
        if (Optional.ofNullable(xid).isPresent()) {
            return SidecarTransactionContext.getTransaction(xid, environment);
        }
        //内部执行的事务(一个方法内的)
        if (TransactionalContext.getContext().get() != null) {
            MybatisTransaction mt = TransactionalContext.getContext().get();
            mt.setDataSource(environment.getDataSource());
            return mt;
        } else {
            //没有事务
            TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            return transactionFactory.newTransaction(environment.getDataSource(), null, autoCommit);
        }
    }


    private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
        if (environment == null || environment.getTransactionFactory() == null) {
            return new ManagedTransactionFactory();
        }
        return environment.getTransactionFactory();
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return null;
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return null;
    }

    @Override
    public SqlSession openSession(ExecutorType execType) {
        return null;
    }

    @Override
    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        return null;
    }

    @Override
    public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
        return null;
    }

    @Override
    public SqlSession openSession(ExecutorType execType, Connection connection) {
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }
}
