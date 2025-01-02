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


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MongoDb {
    private static final Logger logger = LoggerFactory.getLogger(MongoDb.class);

    private MongoClient client;
    private MongoDatabase db;

    //mongodb://localhost:27017,localhost:27018,localhost:27019
    private String mongoDbClient;

    private String mongoDatabase;

    private boolean catEnabled;

    private static final String CAT_TYPE = "mongodb";


    public void init() {
        if (null == mongoDbClient || mongoDbClient.length() == 0) {
            logger.error("[MongoDb.init()] invalid mongoDbClient: {}", mongoDbClient);
            return;
        }

        MongoClientURI connectionString = new MongoClientURI(mongoDbClient);
        client = new MongoClient(connectionString);
        db = client.getDatabase(mongoDatabase);
    }

    public boolean close() {
        try {
            logger.info("mongo client close");
            client.close();
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return db.getCollection(collectionName);
    }

    public <T> MongoCollection<T> getCollection(String collectionName, Class<T> clazz) {
        return db.getCollection(collectionName, clazz);
    }

    public void insert(String collectionName, Document doc) {
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            collection.insertOne(doc);
        } catch (MongoException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
        }

    }

    public void insertMany(String collectionName, List<Document> docList) {
        boolean success = true;
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            collection.insertMany(docList);
        } catch (MongoException e) {
            success = false;
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            success = false;
            logger.error(e.getMessage(), e);
        }
    }

    public Document findFirst(String collectionName) {
        MongoCollection<Document> collection = this.getCollection(collectionName);
        return collection.find().first();
    }

    public Document findFirst(String collectionName, Bson filter) {
        MongoCollection<Document> collection = this.getCollection(collectionName);
        return collection.find(filter).first();
    }

    public <T> T findFirst(String collectionName, Bson filter, Class<T> clazz) {
        MongoCollection<T> collection = this.getCollection(collectionName, clazz);
        return collection.find(filter).first();
    }

    public List<Document> findAll(String collectionName, Bson filter) {
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            List<Document> res = new ArrayList<>();
            for (Document cur : collection.find(filter)) {
                res.add(cur);
            }
            return res;
        } catch (MongoException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public List<Document> findDocumentsWithPagination(String collectionName, Document doc, int page, int pageSize) {
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            List<Document> res = new ArrayList<>();
            FindIterable<Document> data = collection.find(doc).skip((page - 1) * pageSize).limit(pageSize);
            for (Document cur : data) {
                res.add(cur);
            }
            return res;
        } catch (MongoException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public void delete(String collectionName, Document doc) {
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            collection.deleteOne(doc);
        } catch (MongoException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public long count(String collectionName) {
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            return collection.countDocuments();
        } catch (MongoException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
        }
        return -1;
    }

    public MongoClient getClient() {
        return client;
    }

    public void setClient(MongoClient client) {
        this.client = client;
    }

    public MongoDatabase getDb() {
        return db;
    }

    public void setDb(MongoDatabase db) {
        this.db = db;
    }

    public String getMongoDbClient() {
        return mongoDbClient;
    }

    public void setMongoDbClient(String mongoDbClient) {
        this.mongoDbClient = mongoDbClient;
    }

    public String getMongoDatabase() {
        return mongoDatabase;
    }

    public void setMongoDatabase(String mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public boolean isCatEnabled() {
        return catEnabled;
    }

    public void setCatEnabled(boolean catEnabled) {
        this.catEnabled = catEnabled;
    }
}
