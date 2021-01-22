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

package com.xiaomi.youpin.docean.plugin.sql;

import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourcePlugin;
import com.xiaomi.youpin.docean.plugin.datasource.anno.Transactional;
import com.xiaomi.youpin.docean.plugin.sql.interceptor.TransactionalInterceptor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;


/**
 * @author goodjava@qq.com
 */
@DOceanPlugin
@Slf4j
public class SqlPlugin implements IPlugin {


    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init dbplugin");
        Aop.ins().getInterceptorMap().put(Transactional.class, new TransactionalInterceptor());
        List<String> dbNames = ioc.getBean(DatasourcePlugin.DB_NAMES);
        dbNames.stream().forEach(it->addDAO(ioc,it));
    }

    private void addDAO(Ioc ioc, String beanName) {
        Object datasource = ioc.getBean(beanName);
        if (datasource instanceof DataSource) {
            DataSource ds = (DataSource) datasource;
            JdbcTransaction tran = new JdbcTransaction(ds, null, true);
            Db db = new Db(ds, tran);
            ioc.putBean("mione_" + beanName, db);
        }
    }

    @Override
    public String version() {
        return "0.0.2:2020-07-05";
    }
}
