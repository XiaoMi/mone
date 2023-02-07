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

package com.xiaomi.youpin.docean.plugin.db;

import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourcePlugin;
import com.xiaomi.youpin.docean.plugin.db.interceptor.DaoTimeInterceptor;
import com.xiaomi.youpin.docean.plugin.db.interceptor.DoceanInterceptor;
import com.xiaomi.youpin.docean.plugin.db.interceptor.NutzDaoMeshInvoker;
import com.xiaomi.youpin.docean.plugin.db.interceptor.TransactionalInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.impl.NutDao;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@DOceanPlugin
@Slf4j
public class DbPlugin implements IPlugin {

    public static final String INTERCEPTOR_NAME = "nutzDaoInterceptor";

    public static final String DB_OPEN_TRANSACTIONAL_FLAG = "db_open_transactional";

    public static final String PRINT_SQL_FLAG = "db_print_sql";

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init db plugin");
        Config config = ioc.getBean(Config.class);

        // 开启事物
        if (config.get(DB_OPEN_TRANSACTIONAL_FLAG, Boolean.FALSE.toString()).equals("true")) {
            Aop.ins().getInterceptorMap().put(Transactional.class, new TransactionalInterceptor());
        }
        // 是否开启打印sql,默认开启，框架原先就开启了，为了兼容，所以设置了开始
        String printSql = config.get(PRINT_SQL_FLAG, Boolean.TRUE.toString());
        List<String> dbNames = ioc.getBean(DatasourcePlugin.DB_NAMES);
        if (dbNames.size() == 1) {
            NutDao nutDao = new NutDao(ioc.getBean(dbNames.get(0)));
            nutDao.addInterceptor(new DaoTimeInterceptor(printSql));
            if (ioc.containsBean(INTERCEPTOR_NAME)) {
                NutzDaoMeshInvoker invoker = ioc.getBean(INTERCEPTOR_NAME);
                DoceanInterceptor interceptor = new DoceanInterceptor();
                interceptor.setMeshInvoker(invoker);
                nutDao.addInterceptor(interceptor);
            }
            ioc.putBean(nutDao);
        } else {
            dbNames.stream().forEach(it -> addDAO(ioc, it, printSql));
        }
    }

    private void addDAO(Ioc ioc, String beanName, String printSql) {
        Object dataSource = ioc.getBean(beanName);
        if (dataSource instanceof DataSource) {
            NutDao dao = new NutDao((DataSource) dataSource);
            dao.addInterceptor(new DaoTimeInterceptor(printSql));
            if (ioc.containsBean(INTERCEPTOR_NAME)) {
                NutzDaoMeshInvoker invoker = ioc.getBean(INTERCEPTOR_NAME);
                DoceanInterceptor interceptor = new DoceanInterceptor();
                DatasourceConfig config = ioc.getBean(beanName + "_config");
                interceptor.setMeshInvoker(invoker);
                interceptor.setDatasourceConfig(config);
                dao.addInterceptor(interceptor);
            }
            ioc.putBean("nutz_" + beanName, dao);
        }
    }

    @Override
    public String version() {
        return "0.0.1:goodjava@qq.com:20210829";
    }
}
