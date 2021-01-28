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

import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.ClassPathResource;
import com.xiaomi.youpin.docean.common.Resource;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourcePlugin;
import com.xiaomi.youpin.docean.plugin.mybatis.interceptor.DoceanInterceptor;
import com.xiaomi.youpin.docean.plugin.mybatis.interceptor.TransactionalInterceptor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

/**
 * @author goodjava@qq.com
 */
@DOceanPlugin
@Slf4j
public class MybatisPlugin implements IPlugin {

    @SneakyThrows
    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("mybatis plugin init");
        Aop.ins().getInterceptorMap().put(Transactional.class, new TransactionalInterceptor());
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        Config config = ioc.getBean(Config.class);

        List<String> dbNames = ioc.getBean(DatasourcePlugin.DB_NAMES);
        dbNames.stream().forEach(it -> addDAO(ioc, it, config.get("mybatis_mapper_location", "")));
    }

    @SneakyThrows
    private void addDAO(Ioc ioc, String beanName, String mapperLocation) {
        Object dataSource = ioc.getBean(beanName);
        if (dataSource instanceof DataSource) {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource((DataSource) dataSource);
            bean.setMapperLocations(new Resource[]{new ClassPathResource(mapperLocation)});
            bean.setPlugins(new Interceptor[]{new DoceanInterceptor()});
            SqlSessionFactory factory = bean.buildSqlSessionFactory();
            ioc.putBean("mybatis_" + beanName, factory);
        }
    }

    @Override
    public String version() {
        return "0.0.1:2021-01-24";
    }
}
