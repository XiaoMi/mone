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
import com.xiaomi.youpin.docean.plugin.datasource.DatasourcePlugin;
import com.xiaomi.youpin.docean.plugin.db.interceptor.DoceanInterceptor;
import com.xiaomi.youpin.docean.plugin.db.interceptor.TransactionalInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
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

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init dbplugin");
        Aop.ins().getInterceptorMap().put(Transactional.class, new TransactionalInterceptor());

        List<String> dbNames = ioc.getBean(DatasourcePlugin.DB_NAMES);
        if (dbNames.size() == 1) {
            NutDao nutDao = new NutDao(ioc.getBean(dbNames.get(0)));
            nutDao.addInterceptor(new DoceanInterceptor());
            ioc.putBean(nutDao);
        } else {
            dbNames.stream().forEach(it->addDAO(ioc,it));
        }
    }

    private void addDAO(Ioc ioc, String beanName) {
        Object dataSource = ioc.getBean(beanName);
        if (dataSource instanceof DataSource) {
            Dao dao = new NutDao((DataSource) dataSource);
            ioc.putBean("nutz_"+beanName, dao);
        }
    }

}
