package com.xiaomi.mione.prometheus.sql;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shanwb
 * @date 2021-07-09
 */
public class PrometheusMybatisInterceptorWrapper {
    private ConcurrentHashMap<DataSource, PrometeusMybatisInterceptor> mybatisInterceptorMap = new ConcurrentHashMap<>();
    private byte[] lock = new byte[0];

    public PrometheusMybatisInterceptorWrapper(){}

    public PrometeusMybatisInterceptor getInterceptor(DataSource dataSource) {
        PrometeusMybatisInterceptor interceptor = mybatisInterceptorMap.get(dataSource);
        if (null == interceptor) {
            synchronized (lock) {
                DatabaseMetaData dmd = null;
                try {
                    dmd = dataSource.getConnection().getMetaData();
                    String url = dmd.getURL();
                    interceptor = new PrometeusMybatisInterceptor(url);
                    mybatisInterceptorMap.put(dataSource, interceptor);
                } catch (SQLException throwables) {
                    throw new RuntimeException("PrometheusMybatisInterceptorWrapper SQLException:", throwables);
                }
            }
        }

        return interceptor;
    }

}
