package com.xiaomi.mone.app.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.integration.spring.SpringDaoRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 11:34
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.driverClassName}")
    private String driverClass;

    @Value("${spring.datasource.default.initialPoolSize}")
    private Integer defaultInitialPoolSize;

    @Value("${spring.datasource.default.maxPoolSize}")
    private Integer defaultMaxPoolSize;

    @Value("${spring.datasource.default.minialPoolSize}")
    private Integer defaultMinPoolSize;

    @NacosValue(value = "${spring.datasource.username}", autoRefreshed = true)
    private String dataSourceUserName;

    @NacosValue(value = "${spring.datasource.url}", autoRefreshed = true)
    private String dataSourceUrl;

    @NacosValue(value = "${spring.datasource.password}", autoRefreshed = true)
    private String dataSourcePasswd;

    @Value("${server.type}")
    private String serverType;

    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() throws PropertyVetoException, NamingException {
        log.info("DataSourceConfig {} {} {}", serverType, dataSourceUrl, dataSourceUserName);

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

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("masterDataSource") DataSource masterDataSource) {
        JdbcTemplate jt = new JdbcTemplate(masterDataSource);
        return jt;
    }


    @Bean
    public SpringDaoRunner springDaoRunner() {
        return new SpringDaoRunner();
    }

    @Bean
    public Dao dao(@Qualifier("masterDataSource") DataSource masterDataSource, SpringDaoRunner springDaoRunner) {
        NutDao dao = new NutDao(masterDataSource);
        dao.setRunner(springDaoRunner);
        return dao;
    }
}
