package com.xiaomi.youpin.gateway.function.imp;

import com.xiaomi.youpin.gateway.mongodb.MongoDb;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MongoDbImp implements MongoDb {

    @Autowired
    private com.xiaomi.data.push.mongodb.MongoDb mongoDb;

    @Override
    public void insert(String collectionName, Document doc) {
        mongoDb.insert(collectionName, doc);
    }

    @Override
    public void insertMany(String collectionName, List<Document> docList) {
        mongoDb.insertMany(collectionName, docList);
    }

    @Override
    public Document findFirst(String collectionName) {
        return mongoDb.findFirst(collectionName);
    }

    @Override
    public List<Document> findAll(String collectionName, Document doc) {
        return mongoDb.findAll(collectionName, doc);
    }

    @Override
    public void delete(String collectionName, Document doc) {
        mongoDb.delete(collectionName, doc);
    }

    @Override
    public long count(String collectionName) {
        return mongoDb.count(collectionName);
    }


}
