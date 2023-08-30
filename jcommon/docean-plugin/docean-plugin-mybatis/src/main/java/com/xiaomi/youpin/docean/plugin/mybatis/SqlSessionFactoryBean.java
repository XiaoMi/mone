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

import com.xiaomi.youpin.docean.common.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.io.IOException;


/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
@Data
@Slf4j
public class SqlSessionFactoryBean {

    private DataSource dataSource;

    private Interceptor[] plugins;

    private Resource[] mapperLocations;

    private TransactionFactory transactionFactory = new MybatisTransactionFactory();

    private String environment = "docean";

    private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

    private static final String MAP_UNDERSCORE_TO_CAMEL_CASE = "mybatis.map-underscore-to-camel";

    public SqlSessionFactory buildSqlSessionFactory() throws IOException {
        Configuration configuration = new Configuration();
        if (null != this.plugins) {
            for (Interceptor plugin : this.plugins) {
                configuration.addInterceptor(plugin);
                log.debug("Registered plugin: '" + plugin + "'");
            }
        }

        configuration.setEnvironment(new Environment(this.environment, this.transactionFactory, this.dataSource));
        if (StringUtils.equals(System.getenv(MAP_UNDERSCORE_TO_CAMEL_CASE), "true")) {
            configuration.setMapUnderscoreToCamelCase(true);
        }
        if (null != this.mapperLocations) {
            for (Resource mapperLocation : this.mapperLocations) {
                if (mapperLocation == null) {
                    continue;
                }
                try {
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(),
                            configuration, mapperLocation.toString(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                } finally {
                    ErrorContext.instance().reset();
                }
                log.debug("Parsed mapper file: '" + mapperLocation + "'");
            }
        } else {
            log.debug("Property 'mapperLocations' was not specified or no matching resources found");
        }
        return new MybatisSessionFactory(this.sqlSessionFactoryBuilder.build(configuration));

    }

}
