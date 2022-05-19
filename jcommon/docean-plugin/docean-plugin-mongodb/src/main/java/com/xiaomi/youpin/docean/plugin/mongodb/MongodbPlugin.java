package com.xiaomi.youpin.docean.plugin.mongodb;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author zheng.xucn@outlook.com
 * @date 2020/7/03
 */
@DOceanPlugin
@Slf4j
public class MongodbPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init mongodb plugin");
        MongoDb mongoDb = new MongoDb();
        Config config = ioc.getBean(Config.class);
        mongoDb.setMongoDbClient(config.get("mongodb.client", ""));
        mongoDb.setMongoDatabase(config.get("mongodb.database", ""));
        mongoDb.setCatEnabled(config.get("mongodb.cat.enabled", "false").equals("true"));
        mongoDb.init();
        ioc.putBean(mongoDb);
    }

    @Override
    public String version() {
        return "0.0.1:2020-07-04:zheng.xucn@outlook.com";
    }
}
