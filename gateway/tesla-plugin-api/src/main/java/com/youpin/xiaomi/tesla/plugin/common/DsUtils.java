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

package com.youpin.xiaomi.tesla.plugin.common;

import com.google.gson.Gson;
import com.youpin.xiaomi.tesla.plugin.bo.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class DsUtils {

    public static String dsToString(String dsIds, String pluginId, Function<String, String> function) {
        //未设置数据源
        if (null == dsIds || dsIds.trim().equals("")) {
            return "";
        }

        Gson gson = new Gson();
        TeslaDatasourceMap teslaDatasourceMap = new TeslaDatasourceMap();
        teslaDatasourceMap.setPluginId(pluginId);

        Arrays.stream(dsIds.split(",")).forEach(i -> {
            String data = function.apply(i);
            TeslaDs d = new Gson().fromJson(data, TeslaDs.class);
            log.info("dsToString: {} {} {}", i, d, data);
            if (null == d) {
                //忽略掉
                log.warn("ds data is null {}", i);
            } else if (d.getType() == DsType.mysql.ordinal()) {
                //mysql
                DbDatasource ds = new DbDatasource();
                ds.setDriverClass(d.getDriverClass());
                ds.setDataSourceUrl(d.getDataSourceUrl());
                ds.setDataSourceUserName(d.getUserName());
                ds.setDataSourcePasswd(d.getPassWd());
                ds.setDefaultInitialPoolSize(d.getPoolSize());
                ds.setDefaultMaxPoolSize(d.getMaxPoolSize());
                ds.setDefaultMinPoolSize(d.getMinPoolSize());
                teslaDatasourceMap.getMap().put("mysql_ds_" + pluginId, gson.toJson(ds));
            } else if (d.getType() == DsType.dubbo.ordinal()) {
                //dubbo
                DubboDatasource dubboDatasource = new DubboDatasource();
                dubboDatasource.setAppName(d.getAppName());
                dubboDatasource.setRegAddress(d.getRegAddress());
                dubboDatasource.setThreads(d.getThreads());
                dubboDatasource.setApiPackage(d.getApiPackage());
                teslaDatasourceMap.getMap().put("dubbo_ds_" + pluginId, gson.toJson(dubboDatasource));
            } else if (d.getType() == DsType.plugin.ordinal()) {
                //plugin
                PluginDatasource pluginDatasource = new PluginDatasource();
                pluginDatasource.setIocPackage(d.getIocPackage());
                pluginDatasource.setJarPath(d.getJarPath());
                teslaDatasourceMap.getMap().put("plugin_ds_" + pluginId, gson.toJson(pluginDatasource));
            } else if (d.getType() == DsType.redis.ordinal()) {
                //redis
                RedisDatasource redisDatasource = new RedisDatasource();
                redisDatasource.setRedisHosts(d.getDataSourceUrl());
                redisDatasource.setRedisType(d.getRedisType());
                redisDatasource.setPassWd(d.getPassWd());
                teslaDatasourceMap.getMap().put("redis_ds_" + pluginId, gson.toJson(redisDatasource));
            } else if (d.getType() == DsType.nacos.ordinal()) {
                //nacos
                NacosDatasource nacosDatasource = new NacosDatasource();
                nacosDatasource.setServerAddr(d.getDataSourceUrl());
                nacosDatasource.setDataId(d.getNacosDataId());
                nacosDatasource.setGroup(d.getNacosGroup());
                teslaDatasourceMap.getMap().put("nacos_ds_" + pluginId, gson.toJson(nacosDatasource));
            } else if (d.getType() == DsType.mongoDb.ordinal()) {
                //mongodb
                MongoDbDatasource mongoDbDatasource = new MongoDbDatasource();
                mongoDbDatasource.setMongoDbHosts(d.getDataSourceUrl());
                mongoDbDatasource.setMongoDbDatabase(d.getMongoDatabase());
                teslaDatasourceMap.getMap().put("mongoDb_ds_" + pluginId, gson.toJson(mongoDbDatasource));
            }
        });

        return new Gson().toJson(teslaDatasourceMap);
    }
}
