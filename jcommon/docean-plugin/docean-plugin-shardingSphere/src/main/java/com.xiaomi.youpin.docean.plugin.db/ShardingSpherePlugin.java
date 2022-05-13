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
import com.xiaomi.youpin.docean.plugin.db.interceptor.TransactionalInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.shardingjdbc.api.yaml.YamlShardingDataSourceFactory;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@DOceanPlugin
@Slf4j
public class ShardingSpherePlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init ShardingSpherePlugin");
        Aop.ins().getInterceptorMap().put(Transactional.class, new TransactionalInterceptor());
        DataSource dataSource = dataSource();
        if (dataSource != null) {
            NutDao dao = new NutDao(dataSource);
            ioc.putBean(dao);
            ioc.putBean(Dao.class.getName(),dao);
            ioc.putBean(dataSource);
            ioc.putBean(DataSource.class.getName(),dataSource);
        }
    }

    private DataSource dataSource() {
        DataSource dataSource = null;
        try {
            dataSource= YamlShardingDataSourceFactory.createDataSource(getFile("/shardingSphere/sharding-databases-tables.yaml"));
        } catch (SQLException e) {
            log.error(e.toString());
        } catch (IOException e) {
            log.error(e.toString());
        }
        return dataSource;
    }

    private File getFile(String fileName) {
        return new File(ShardingSpherePlugin.class.getResource(fileName).getFile());
    }

}
