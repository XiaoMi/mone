package com.xiaomi.youpin.prometheus.agent.config;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DaoConfig {

    @Bean
    public Dao dao(@Qualifier("dataSource") DataSource masterDataSource) {
        NutDao dao = new NutDao(masterDataSource);
        return dao;
    }

}
