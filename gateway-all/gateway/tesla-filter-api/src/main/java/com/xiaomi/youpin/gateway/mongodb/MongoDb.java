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

package com.xiaomi.youpin.gateway.mongodb;

import org.bson.Document;

import java.util.List;

public interface MongoDb {

    void insert(String collectionName, Document doc);

    void insertMany(String collectionName, List<Document> docList);

    Document findFirst(String collectionName);

    List<Document> findAll(String collectionName, Document doc);

    void delete(String collectionName, Document doc);

    long count(String collectionName);

}
