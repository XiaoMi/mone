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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.xiaomi.youpin.cat.CatPlugin;
import org.bson.Document;
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

    public void insert(String collectionName, Document doc) {
        CatPlugin cat = new CatPlugin("insert", catEnabled, CAT_TYPE);
        boolean success = true;
        cat.before(null);
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            collection.insertOne(doc);
        } catch (MongoException e) {
            success = false;
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            success = false;
            logger.error(e.getMessage(), e);
        } finally {
            cat.after(success);
        }

    }

    public void insertMany(String collectionName, List<Document> docList) {
        CatPlugin cat = new CatPlugin("insertMany", catEnabled, CAT_TYPE);
        boolean success = true;
        cat.before(null);
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            collection.insertMany(docList);
        } catch (MongoException e) {
            success = false;
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            success = false;
            logger.error(e.getMessage(), e);
        } finally {
            cat.after(success);
        }
    }

    public Document findFirst(String collectionName) {
        CatPlugin cat = new CatPlugin("findFirst", catEnabled, CAT_TYPE);
        boolean success = true;
        cat.before(null);
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            return collection.find().first();
        } catch (MongoException e) {
            success = false;
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            success = false;
            logger.error(e.getMessage(), e);
        } finally {
            cat.after(success);
        }
        return null;
    }

    public List<Document> findAll(String collectionName, Document doc) {
        CatPlugin cat = new CatPlugin("findAll", catEnabled, CAT_TYPE);
        boolean success = true;
        cat.before(null);
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            MongoCursor<Document> cursor = collection.find().iterator();
            List<Document> res = new ArrayList<>();
            for (Document cur : collection.find()) {
                res.add(cur);
            }

            return res;
        } catch (MongoException e) {
            success = false;
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            success = false;
            logger.error(e.getMessage(), e);
        } finally {
            cat.after(success);
        }
        return null;
    }

    public void delete(String collectionName, Document doc) {
        CatPlugin cat = new CatPlugin("delete", catEnabled, CAT_TYPE);
        boolean success = true;
        cat.before(null);
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            collection.deleteOne(doc);
        } catch (MongoException e) {
            success = false;
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            success = false;
            logger.error(e.getMessage(), e);
        } finally {
            cat.after(success);
        }
    }

    public long count(String collectionName) {
        CatPlugin cat = new CatPlugin("count", catEnabled, CAT_TYPE);
        boolean success = true;
        cat.before(null);
        try {
            MongoCollection<Document> collection = this.getCollection(collectionName);
            return collection.countDocuments();
        } catch (MongoException e) {
            success = false;
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            success = false;
            logger.error(e.getMessage(), e);
        } finally {
            cat.after(success);
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
