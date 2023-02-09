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

package com.xiaomi.youpin.tesla.plug;


import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.xiaomi.data.push.mongodb.MongoDb;
import com.xiaomi.data.push.nacos.NacosConfig;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.tesla.plug.bo.ApiInfo;
import com.xiaomi.youpin.tesla.plug.service.TestService;
import com.youpin.xiaomi.tesla.plugin.bo.Request;
import com.youpin.xiaomi.tesla.plugin.bo.RequestContext;
import com.youpin.xiaomi.tesla.plugin.bo.Response;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.loader.annotation.Inject;
import org.pf4j.Extension;

/**
 * @author goodjava@qq.com
 * 业务处理的入口
 */
@Slf4j
@Extension
public class DemoHandler extends BaseHandler<String> {

    private static final String version = "0.0.3";

    @Inject
    private TestService ts;

    @Inject("dao")
    private NutDao teslaDao;

    @Inject("config")
    private NacosConfig config;

    @Inject("redis")
    private Redis redis;

    @Inject("mongo")
    private MongoDb mongo;


    @Override
    public Response<String> execute(RequestContext context, Request request) {
        //db
        ApiInfo info = teslaDao.fetch(ApiInfo.class, 1);


        int sumResult = ts.sum(11, 33);
        //dubbo
        String pong = ts.ping();


        //nacos
        String name = config.getConfig("testConfig");
        log.info("config:{}", name);

        //redis
        redis.set("name", "dingpei");
        String v = redis.get("name");
        log.info("redis:{}", v);

        //mongodb
        Document document = new Document("title", "MongoDB");
        mongo.getCollection("tesla_test").insertOne(document);
        FindIterable<Document> list = mongo.getCollection("tesla_test").find();
        list.forEach((Block<? super Document>) it -> {
            log.info(it.toJson());
        });

        return new Response<>(0, "msg:" + name, version + "\n" + info + "\ncache:" + "" + "\nsum:" + sumResult + "\ndubbo:" + pong);
    }


    @Override
    public String url() {
        return "/xxxx/api/demo/plugin";
//        return "/demo/go";
    }

}
