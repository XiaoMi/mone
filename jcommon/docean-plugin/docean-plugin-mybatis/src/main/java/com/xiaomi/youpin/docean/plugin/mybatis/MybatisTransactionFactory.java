package com.xiaomi.youpin.docean.plugin.mybatis;

import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author goodjava@qq.com
 * @date 2020/7/7
 */
public class MybatisTransactionFactory  implements TransactionFactory {

    @Override
    public void setProperties(Properties props) {
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return null;
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, org.apache.ibatis.session.TransactionIsolationLevel level, boolean autoCommit) {
        return new MybatisTransaction(dataSource, level, autoCommit);
    }


}
