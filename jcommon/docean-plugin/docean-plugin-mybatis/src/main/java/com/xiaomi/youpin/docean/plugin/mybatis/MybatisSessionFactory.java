package com.xiaomi.youpin.docean.plugin.mybatis;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;

import java.sql.Connection;

/**
 * @author goodjava@qq.com
 * @date 2020/7/7
 */
public class MybatisSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    private DefaultSqlSessionFactory sqlSessionFactory;

    public MybatisSessionFactory(DefaultSqlSessionFactory sqlSessionFactory) {
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
            Transaction tx = null;
            if (TransactionalContext.getContext().get() != null) {
                MybatisTransaction _tx = TransactionalContext.getContext().get();
                _tx.setDataSource(environment.getDataSource());
                tx = _tx;
            } else {
                TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
                tx = transactionFactory.newTransaction(environment.getDataSource(), null, autoCommit);
            }
            final Executor executor = configuration.newExecutor(tx, configuration.getDefaultExecutorType());
            return new DefaultSqlSession(configuration, executor, autoCommit);
        } finally {
            ErrorContext.instance().reset();
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
