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

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.data.push.common.Result;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @author lyc@qq.com
 * @author shanwenbang
 * @date 2020/7/2
 */
@Slf4j
@DOceanPlugin(order = 10)
public class DatasourcePlugin implements IPlugin {
    public static final String PREFIX = "ds_";
    public static final String DB_NAMES = "DB_NAMES";

    private static final String DB_DS_NAME = "db_ds_name";
    private static final String GATEWAY_HOST = "mi_gateway_host";
    private static final String GET_DS_URI = "/mtop/arch/plugin/datasource/getDsByNames";

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init datasource plugin");
        List dbNames = new ArrayList();
        Config c = ioc.getBean(Config.class);
        //本地配置数据源
        initByConfig(ioc, c, dbNames);
        //只配置数据源名称，通过远程接口获取详情
        initByDsNames(ioc, c, dbNames);
        ioc.putBean(DB_NAMES, dbNames);
    }

    private void initByConfig(final Ioc ioc, final Config c, final List dbNames) {
        String dsType = c.get("ds_type", "");

        if (StringUtils.isNotEmpty(c.get("db_url", ""))) {
            String dbName = c.get("db_name", PREFIX + "0");
            DatasourceConfig dsConfig = generateDatasourceConfig("", c);
            ioc.putBean(dbName, dataSource(dsConfig, dsType));
            ioc.putBean(dbName + "_config", dsConfig);
            dbNames.add(dbName);
        } else {
            Map<String, String> dbConfMap = c.getByPrefix(PREFIX, false);
            Set<String> dbGroupSet = dbConfMap.keySet()
                    .stream()
                    .map(k -> {
                        if (k.indexOf(".") < 0) {
                            return null;
                        }
                        String groupKey = k.substring(0, k.indexOf("."));
                        return groupKey;
                    })
                    .filter(k -> k != null)
                    .collect(Collectors.toSet());


            dbGroupSet.stream()
                    .forEach(groupKey -> {
                                String prefix = groupKey + ".";
                                if (StringUtils.isNotEmpty(c.get(prefix + "db_url", ""))) {
                                    String dbName = c.get(prefix + "db_name", groupKey);
                                    DatasourceConfig dsConfig = generateDatasourceConfig(prefix, c);
                                    ioc.putBean(dbName, dataSource(dsConfig, dsType));
                                    ioc.putBean(dbName + "_config", dsConfig);
                                    dbNames.add(dbName);
                                }
                            }
                    );
        }
    }

    private void initByDsNames(final Ioc ioc, final Config c, final List dbNames) {
        List<DatasourceMeta> extendDsList = getDsByNames(c);
        if (null == extendDsList || extendDsList.size() == 0) {
            return;
        }

        extendDsList.forEach(datasourceMeta -> {
            String dbName = datasourceMeta.getName();
            // DbName相同，以本地配置为准
            if (!dbNames.contains(dbName)) {
                DatasourceConfig dsConfig = generateDatasourceConfig(datasourceMeta);
                ioc.putBean(dbName, dataSource(dsConfig));
                ioc.putBean(dbName + "_config", dsConfig);
                dbNames.add(dbName);
            }
        });
    }

    private List<DatasourceMeta> getDsByNames(Config config) {
        String dbNames = config.get(DB_DS_NAME, "");
        if (StringUtils.isBlank(dbNames)) {
            return Lists.newArrayList();
        }

        String gatewayHost = config.get(GATEWAY_HOST, "");
        if (StringUtils.isBlank(gatewayHost)) {
            throw new RuntimeException(String.format("init datasource by %s:%s failed, config \"%s\" can not be null", DB_DS_NAME, dbNames, GATEWAY_HOST));
            //return Lists.newArrayList();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("names", Arrays.asList(dbNames.split(",")));

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Yp-App-Token", "xxx");

        String dbNamesContent = HttpClientV2.post(gatewayHost + GET_DS_URI, "[" + new Gson().toJson(body) + "]", headers);
        if (StringUtils.isBlank(dbNamesContent)) {
            return Lists.newArrayList();
        }

        Result<List<DatasourceMeta>> result = new Gson().fromJson(dbNamesContent, new TypeToken<Result<List<DatasourceMeta>>>() {
        }.getType());
        List<DatasourceMeta> datasourceMetaList = result.getData();
        return datasourceMetaList;
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

    private DatasourceConfig generateDatasourceConfig(String prefix, Config c) {
        DatasourceConfig config = new DatasourceConfig();
        config.setName(c.get(prefix + "db_name", ""));
        config.setDataSourcePasswd(c.get(prefix + "db_pwd", ""));
        config.setDataSourceUrl(c.get(prefix + "db_url", ""));
        config.setDataSourceUserName(c.get(prefix + "db_user_name", ""));
        config.setDefaultInitialPoolSize(Integer.valueOf(c.get(prefix + "db_pool_size", "1")));
        config.setDefaultMaxPoolSize(Integer.valueOf(c.get(prefix + "db_pool_size", "1")));
        config.setDefaultMinPoolSize(Integer.valueOf(c.get(prefix + "db_pool_size", "1")));
        config.setDriverClass("com.mysql.jdbc.Driver");
        return config;
    }

    private DatasourceConfig generateDatasourceConfig(DatasourceMeta datasourceMeta) {
        DatasourceConfig config = new DatasourceConfig();
        config.setName(datasourceMeta.getName());
        config.setDataSourcePasswd(datasourceMeta.getPassWd());
        config.setDataSourceUrl(datasourceMeta.getDataSourceUrl());
        config.setDataSourceUserName(datasourceMeta.getUserName());
        config.setDefaultInitialPoolSize(0 == datasourceMeta.getPoolSize() ? 1 : datasourceMeta.getPoolSize());
        config.setDefaultMaxPoolSize(0 == datasourceMeta.getMaxPoolSize() ? 1 : datasourceMeta.getMaxPoolSize());
        config.setDefaultMinPoolSize(0 == datasourceMeta.getMinPoolSize() ? 1 : datasourceMeta.getMinPoolSize());
        config.setDriverClass(datasourceMeta.getDriverClass());
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
        initComboPooledDatasouce(dataSource);
        return dataSource;
    }


    private DataSource dataSource(DatasourceConfig c, String type) {
        if (type.equals("hikari")) {
            log.info("use hikari ds");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(c.getDataSourceUrl());
            config.setUsername(c.getDataSourceUserName());
            config.setPassword(c.getDataSourcePasswd());
            config.setDriverClassName(c.getDriverClass());
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.setMinimumIdle(c.getDefaultMinPoolSize());
            config.setMaximumPoolSize(c.getDefaultMaxPoolSize());
            return new HikariDataSource(config);
        } else if (type.equals("druid")) {
            log.info("use druid ds");
            DruidDataSource datasource = new DruidDataSource();
            datasource.setUrl(c.getDataSourceUrl());
            datasource.setUsername(c.getDataSourceUserName());
            datasource.setPassword(c.getDataSourcePasswd());
            datasource.setDriverClassName(c.getDriverClass());
            datasource.setInitialSize(c.getDefaultInitialPoolSize());
            datasource.setMinIdle(c.getDefaultMinPoolSize());
            datasource.setMaxActive(c.getDefaultMaxPoolSize());
        }
        //c3p0
        return dataSource(c);
    }

    private void initComboPooledDatasouce(ComboPooledDataSource dataSource) {
        log.info("init c3p0 datasource");
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setTestConnectionOnCheckout(true);
        dataSource.setPreferredTestQuery("select 1");
        dataSource.setMaxIdleTime(180);
        dataSource.setIdleConnectionTestPeriod(60);
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> Safe.run(() -> log.debug("datasource info NumConnections:{} NumBusyConnections:{} NumIdleConnections:{} NumUnclosedOrphanedConnections:{}",
                dataSource.getNumConnectionsDefaultUser(),
                dataSource.getNumBusyConnectionsDefaultUser(),
                dataSource.getNumIdleConnectionsDefaultUser(),
                dataSource.getNumUnclosedOrphanedConnectionsDefaultUser()
        )), 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public String version() {
        return "0.0.1:goodjava@qq.com:20210829";
    }

}
