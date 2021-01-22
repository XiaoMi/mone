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

package com.xiaomi.youpin.tesla.dev.bootstrap;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.xiaomi.data.push.mongodb.MongoDb;
import com.xiaomi.data.push.nacos.NacosConfig;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.tesla.plug.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.Ioc;
import org.pf4j.PluginException;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class Bootstrap {

    public static void main(String[] args) throws IOException, PluginException {
        Ioc ioc = new PluginInit().init("databank", "");

        NutDao dao = ioc.get(NutDao.class, "dao");
        System.out.println(dao);

        DemoService ts = ioc.get(DemoService.class);
        System.out.println(ts.sum(11, 22));


        Redis redis = ioc.get(Redis.class, "redis");
        redis.set("name", "zzy");
        System.out.println(redis.get("name"));


        MongoDb mongo = ioc.get(MongoDb.class, "mongo");

        Document document = new Document("title", "MongoDB");
        mongo.getCollection("tesla_test").insertOne(document);
        FindIterable<Document> list = mongo.getCollection("tesla_test").find();
        list.forEach((Block<? super Document>) it -> {
            log.info(it.toJson());
        });


        NacosConfig config = ioc.get(NacosConfig.class, "config");
        log.info(config.getConfig("testConfig"));


    }

}
