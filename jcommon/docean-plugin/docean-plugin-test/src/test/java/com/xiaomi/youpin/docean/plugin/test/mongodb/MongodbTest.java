package com.xiaomi.youpin.docean.plugin.test.mongodb;

import com.mongodb.Block;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.mongodb.MongoDb;
import org.bson.Document;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2024/3/22 16:15
 */
public class MongodbTest {


    @Test
    public void test1() {
        MongoDb mongoDb = getMongoDb();
        Document data = mongoDb.findFirst("book");
        System.out.println(data);
    }

    private static MongoDb getMongoDb() {
        Config config = new Config();
        config.put("mongodb.client", "mongodb://localhost:27017");
        config.put("mongodb.database", "book");
        Ioc.ins().putBean(config).init("com.xiaomi.youpin.docean.plugin.mongodb");
        MongoDb mongoDb = Ioc.ins().getBean(MongoDb.class);
        return mongoDb;
    }

    @Test
    public void testInsert() {
        MongoDb mongoDb = getMongoDb();
        Document document = new Document();
        document.put("name", "水浒");
        mongoDb.insert("book", document);
    }

    @Test
    public void testFind() {
        MongoDb mongoDb = getMongoDb();
        mongoDb.findAll("book", null).forEach(it -> {
            System.out.println(it);
        });
    }

    @Test
    public void testFindWithPage() {
        MongoDb mongoDb = getMongoDb();
        int page = 1;
        int pageSize = 1;
        mongoDb.getCollection("book").find().skip((page - 1) * pageSize).limit(pageSize).forEach((Block<Document>) document -> {
            System.out.println(document);
        });
    }
}
