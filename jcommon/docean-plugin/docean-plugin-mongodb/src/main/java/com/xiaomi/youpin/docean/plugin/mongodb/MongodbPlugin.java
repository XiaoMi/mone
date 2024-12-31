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

package com.xiaomi.youpin.docean.plugin.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
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
        Config config = ioc.getBean(Config.class);
        MongoClient mongoClient = MongoClients.create(config.get("mongodb.client", ""));
        Datastore datastore = Morphia.createDatastore(mongoClient, config.get("mongodb.database", ""));
        String packagePath = config.get("mongodb.package", "run.mone.bo");
        datastore.getMapper().mapPackage(packagePath);
        datastore.ensureIndexes();
        ioc.putBean(Datastore.class.getName(), datastore);
    }

    @Override
    public String version() {
        return "0.0.1:2020-07-04:goodjava@qq.com";
    }

}
