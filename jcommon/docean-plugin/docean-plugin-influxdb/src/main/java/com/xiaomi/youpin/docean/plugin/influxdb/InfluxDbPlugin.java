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

package com.xiaomi.youpin.docean.plugin.influxdb;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import java.util.Set;

/**
 * @author zhangjunyi
 * created on 2020/8/14 3:38 下午
 */
@Slf4j
@DOceanPlugin
public class InfluxDbPlugin implements IPlugin{
    private  static  String PREFIX="influx_";
    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
       log.info("influxdb plugin init");
       Config c = ioc.getBean(Config.class);
       ioc.putBean(InfluxDB.class.getName(),genBean(genConfig(c)));
    }
    public InfluxDbConifg genConfig(Config c){
        InfluxDbConifg config = new InfluxDbConifg();
        config.setDbUrl(c.get(PREFIX+"url",""));
        config.setDatabaseName(c.get(PREFIX+"databaseName","'"));
        config.setUsername(c.get(PREFIX+"username",""));
        config.setPassword(c.get(PREFIX+"password",""));
        String  retentionPolicy = c.get(PREFIX+"rententionPolicy","");
        if(StringUtils.isNotEmpty(retentionPolicy)){
            config.setRetentionPolicy(retentionPolicy);
        }
        return  config;
    }
    public InfluxDB genBean(InfluxDbConifg config){
        InfluxDB db = InfluxDBFactory.connect(config.getDbUrl(),config.getUsername(),config.getPassword());
        db.setDatabase(config.getDatabaseName());
        if(StringUtils.isNotEmpty(config.getRetentionPolicy())){
            db.setRetentionPolicy(config.getRetentionPolicy());
        }
        return  db;
    }

    @Override
    public String version() {
        return "version:0.0.1,2020-08-14 16:03:08";
    }
}
