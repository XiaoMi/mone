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
