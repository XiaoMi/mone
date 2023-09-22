package com.xiaomi.youpin.prometheus.agent.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class DataSourceConfiguration {
    @NacosValue("${spring.datasource.username}")
    private String dataSourceUserName;

    @NacosValue("${spring.datasource.url}")
    private String dataSourceUrl;

    @NacosValue("${spring.datasource.password}")
    private String dataSourcePasswd;

    @NacosValue("${spring.datasource.driverClassName}")
    private String driverClass;

    @NacosValue("${spring.datasource.default.initialPoolSize}")
    private Integer defaultInitialPoolSize;

    @NacosValue("${spring.datasource.default.maxPoolSize}")
    private Integer defaultMaxPoolSize;

    @NacosValue("${spring.datasource.default.minialPoolSize}")
    private Integer defaultMinPoolSize;

    @Value("${server.type}")
    private String serverType;

    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() throws PropertyVetoException, NamingException {
        log.info("DataSourceConfig {} {} {} {}", serverType, dataSourceUrl, dataSourceUserName);

        log.info("DataSourceConfig {} {} {} {}", serverType, dataSourceUrl, dataSourceUserName);

        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(dataSourceUrl);
        dataSource.setUser(dataSourceUserName);
        dataSource.setPassword(dataSourcePasswd);
        dataSource.setInitialPoolSize(defaultInitialPoolSize);
        dataSource.setMaxPoolSize(defaultMaxPoolSize);
        dataSource.setMinPoolSize(defaultMinPoolSize);

        setDatasouce(dataSource);

        return dataSource;
    }

    private void setDatasouce(ComboPooledDataSource dataSource) {
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setTestConnectionOnCheckout(false);
        dataSource.setPreferredTestQuery("select 1");
        dataSource.setIdleConnectionTestPeriod(180);
    }

    @Bean(name = "masterTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager() throws PropertyVetoException, NamingException {
        return new DataSourceTransactionManager(masterDataSource());
    }
}
