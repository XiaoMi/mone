
package run.mone.mongo.function;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Slf4j
public class MongoDBFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "mongoDBOperation";

    private String desc = "MongoDB operations including create database, create collection, create document, query documents, insert data, update document, list databases, list collections, and describe collection structure";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["createDatabase", "createCollection", "createDocument", "queryDocuments", "insertData", "updateDocument", "listDatabases", "listCollections", "describeCollection"],
                        "description":"The operation to perform on MongoDB"
                    },
                    "databaseName": {
                        "type": "string",
                        "description":"The name of the database"
                    },
                    "collectionName": {
                        "type": "string",
                        "description":"The name of the collection"
                    },
                    "document": {
                        "type": "object",
                        "description":"The document to insert or update"
                    },
                    "query": {
                        "type": "object",
                        "description":"The query for finding documents"
                    },
                    "page": {
                        "type": "integer",
                        "description":"The page number for pagination"
                    },
                    "pageSize": {
                        "type": "integer",
                        "description":"The number of documents per page"
                    },
                    "update": {
                        "type": "object",
                        "description":"The update operations to apply"
                    }
                },
                "required": ["operation", "databaseName"]
            }
            """;

    private MongoClient mongoClient;

    public MongoDBFunction() {
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");
        String databaseName = (String) arguments.get("databaseName");

        log.info("operation: {} databaseName: {}", operation, databaseName);

        try {
            String result = switch (operation) {
                case "createDatabase" -> createDatabase(databaseName);
                case "createCollection" -> createCollection(databaseName, (String) arguments.get("collectionName"));
                case "createDocument" -> createDocument(databaseName, (String) arguments.get("collectionName"), (Map<String, Object>) arguments.get("document"));
                case "queryDocuments" -> queryDocuments(databaseName, (String) arguments.get("collectionName"), (Map<String, Object>) arguments.get("query"), (Integer) arguments.get("page"), (Integer) arguments.get("pageSize"));
                case "insertData" -> insertData(databaseName, (String) arguments.get("collectionName"), (Map<String, Object>) arguments.get("document"));
                case "updateDocument" -> updateDocument(databaseName, (String) arguments.get("collectionName"), (Map<String, Object>) arguments.get("query"), (Map<String, Object>) arguments.get("update"));
                case "listDatabases" -> listDatabases();
                case "listCollections" -> listCollections(databaseName);
                case "describeCollection" -> describeCollection(databaseName, (String) arguments.get("collectionName"));
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    private String createDatabase(String databaseName) {
        mongoClient.getDatabase(databaseName);
        return "Database created successfully: " + databaseName;
    }

    private String createCollection(String databaseName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        database.createCollection(collectionName);
        return "Collection created successfully: " + collectionName;
    }

    private String createDocument(String databaseName, String collectionName, Map<String, Object> document) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.insertOne(new Document(document));
        return "Document created successfully";
    }

    private String queryDocuments(String databaseName, String collectionName, Map<String, Object> query, Integer page, Integer pageSize) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        
        FindIterable<Document> findIterable = collection.find(new Document(query));
        if (page != null && pageSize != null) {
            findIterable.skip((page - 1) * pageSize).limit(pageSize);
        }

        List<Document> documents = new ArrayList<>();
        for (Document doc : findIterable) {
            documents.add(doc);
        }

        return documents.toString();
    }

    private String insertData(String databaseName, String collectionName, Map<String, Object> document) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.insertOne(new Document(document));
        return "Data inserted successfully";
    }

    private String updateDocument(String databaseName, String collectionName, Map<String, Object> query, Map<String, Object> update) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Bson filterBson = Filters.and(query.entrySet().stream()
                .map(entry -> Filters.eq(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
        Bson updateBson = new Document("$set", new Document(update));
        collection.updateOne(filterBson, updateBson, new UpdateOptions().upsert(true));
        return "Document updated successfully";
    }

    private String listDatabases() {
        List<String> databaseNames = mongoClient.listDatabaseNames().into(new ArrayList<>());
        return String.join(", ", databaseNames);
    }

    private String listCollections(String databaseName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        List<String> collectionNames = database.listCollectionNames().into(new ArrayList<>());
        return String.join(", ", collectionNames);
    }

    private String describeCollection(String databaseName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document firstDocument = collection.find().first();
        if (firstDocument == null) {
            return "Collection is empty";
        }
        return firstDocument.keySet().toString();
    }
}
