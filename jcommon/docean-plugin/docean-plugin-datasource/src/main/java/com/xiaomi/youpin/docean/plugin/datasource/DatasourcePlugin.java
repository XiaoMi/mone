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

package com.xiaomi.youpin.docean.plugin.datasource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @author lyc@qq.com
 * @date 2020/7/2
 */
@DOceanPlugin(order = 10)
public class DatasourcePlugin implements IPlugin {
    public static final String PREFIX = "ds_";
    public static final String DB_NAMES = "DB_NAMES";

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {

        List dbNames = new ArrayList();
        Config c = ioc.getBean(Config.class);
        if (StringUtils.isNotEmpty(c.get("db_url", ""))) {
            String dbName = c.get("db_name", PREFIX + "0");
            ioc.putBean(dbName, dataSource(generateDatasourceConfig(0, c, false)));
            dbNames.add(dbName);
        } else {
            IntStream.range(0, 10).forEach(i -> {
                if (StringUtils.isNotEmpty(c.get(PREFIX + i + ".db_url", ""))) {
                    String dbName = c.get(PREFIX + i + ".db_name", PREFIX + i);
                    ioc.putBean(dbName, dataSource(generateDatasourceConfig(i, c, true)));
                    dbNames.add(dbName);
                }
            });
        }
        ioc.putBean(DB_NAMES, dbNames);
    }

    /**
     * 关闭指定数据源
     *
     * @param config
     */
    public void remove(DatasourceConfig config) {
        ComboPooledDataSource ds = Ioc.ins().getBean("ds:" + config.getName());
        ds.close();
        Ioc.ins().remove("ds:" + config.getName());
    }


    /**
     * 添加数据源
     *
     * @param config
     * @return
     */
    public DataSource add(DatasourceConfig config) {
        DataSource ds = dataSource(config);
        Ioc.ins().putBean("ds:" + config.getName(), ds);
        return ds;
    }

    private DatasourceConfig generateDatasourceConfig(int datasourceNum, Config c, boolean multipleEnable) {
        DatasourceConfig config = new DatasourceConfig();
        String prefix = "";
        if (multipleEnable) {
            prefix = PREFIX + datasourceNum + ".";
        }
        config.setDataSourcePasswd(c.get(prefix + "db_pwd", ""));
        config.setDataSourceUrl(c.get(prefix + "db_url", ""));
        config.setDataSourceUserName(c.get(prefix + "db_user_name", ""));
        config.setDefaultInitialPoolSize(Integer.valueOf(c.get(prefix + "db_pool_size", "1")));
        config.setDefaultMaxPoolSize(Integer.valueOf(c.get(prefix + "db_pool_size", "1")));
        config.setDefaultMinPoolSize(Integer.valueOf(c.get(prefix + "db_pool_size", "1")));
        config.setDriverClass("com.mysql.jdbc.Driver");
        return config;
    }

    @SneakyThrows
    private DataSource dataSource(DatasourceConfig config) {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(config.getDriverClass());
        dataSource.setJdbcUrl(config.getDataSourceUrl());
        dataSource.setUser(config.getDataSourceUserName());
        dataSource.setPassword(config.getDataSourcePasswd());
        dataSource.setInitialPoolSize(config.getDefaultInitialPoolSize());
        dataSource.setMaxPoolSize(config.getDefaultMaxPoolSize());
        dataSource.setMinPoolSize(config.getDefaultMinPoolSize());
        initDatasouce(dataSource);
        return dataSource;
    }

    private void initDatasouce(ComboPooledDataSource dataSource) {
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setTestConnectionOnCheckout(false);
        dataSource.setPreferredTestQuery("select 1");
        dataSource.setIdleConnectionTestPeriod(180);
    }

    @Override
    public String version() {
        return "0.0.1:rq:zz:lyc";
    }
}
