package com.xiaomi.youpin.docean.plugin.mybatisplus;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.xiaomi.youpin.docean.common.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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

    private MybatisSqlSessionFactoryBuilder sqlSessionFactoryBuilder = new MybatisSqlSessionFactoryBuilder();


    public SqlSessionFactory buildSqlSessionFactory() throws IOException {
        Configuration configuration = new MybatisConfiguration();
        if (null != this.plugins) {
            for (Interceptor plugin : this.plugins) {
                configuration.addInterceptor(plugin);
                log.debug("Registered plugin: '" + plugin + "'");
            }
        }

        configuration.setEnvironment(new Environment(this.environment, this.transactionFactory, this.dataSource));

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
