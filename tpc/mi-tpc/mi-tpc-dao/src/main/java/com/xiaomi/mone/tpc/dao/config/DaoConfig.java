package com.xiaomi.mone.tpc.dao.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.zaxxer.hikari.HikariDataSource;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 19:54
 */
@Configuration
public class DaoConfig {

    @NacosValue("${datasource.url}")
    private String url;
    @NacosValue("${datasource.username}")
    private String username;
    @NacosValue("${datasource.pwd}")
    private String pwd;
    @NacosValue("${datasource.min-idle:10}")
    private int minIdle;
    @NacosValue("${datasource.max-pool-size:20}")
    private int maxPoolSize;
    @NacosValue("${datasource.idle-timeout:60000}")
    private int idleTimeout;
    @NacosValue("${datasource.pool-name:hikar-mysql}")
    private String poolName;

    @Bean
    public DataSource dataSource() {
        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(pwd);
        datasource.setDriverClassName("com.mysql.jdbc.Driver");
        datasource.setMinimumIdle(minIdle);
        datasource.setMaximumPoolSize(maxPoolSize);
        datasource.setAutoCommit(true);
        datasource.setIdleTimeout(idleTimeout);
        datasource.setPoolName(poolName);
        return datasource;
    }

    @Bean
    public Dao dao(@Qualifier("dataSource") DataSource masterDataSource) {
        NutDao dao = new NutDao(masterDataSource);
        return dao;
    }

}
