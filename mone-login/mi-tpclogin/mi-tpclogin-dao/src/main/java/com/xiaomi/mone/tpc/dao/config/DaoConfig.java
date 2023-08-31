package com.xiaomi.mone.tpc.dao.config;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.integration.spring.SpringDaoRunner;
import org.nutz.trans.Trans;
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

    @Bean
    public Dao dao(@Qualifier("dataSource") DataSource masterDataSource) {
        NutDao dao = new NutDao(masterDataSource);
        return dao;
    }

}
