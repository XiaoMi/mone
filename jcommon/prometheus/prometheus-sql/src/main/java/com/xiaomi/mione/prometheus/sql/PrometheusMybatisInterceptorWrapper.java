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
