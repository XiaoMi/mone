package run.mone.hive.memory.longterm.vectorstore.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import lombok.Data;
import run.mone.hive.memory.longterm.config.VectorStoreConfig;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreBase;
import run.mone.hive.memory.longterm.model.MemoryItem;

import tech.amikos.chromadb.Client;
import tech.amikos.chromadb.Collection;
import tech.amikos.chromadb.Collection.GetResult;
import tech.amikos.chromadb.EFException;
import tech.amikos.chromadb.Embedding;
import tech.amikos.chromadb.embeddings.DefaultEmbeddingFunction;
import tech.amikos.chromadb.embeddings.EmbeddingFunction;
import tech.amikos.chromadb.embeddings.WithParam;
import tech.amikos.chromadb.embeddings.openai.CreateEmbeddingRequest;
import tech.amikos.chromadb.embeddings.openai.CreateEmbeddingResponse;
import tech.amikos.chromadb.handler.ApiException;

import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import java.io.IOException;
import java.time.LocalDateTime;

import static tech.amikos.chromadb.Constants.JSON;

@Slf4j
@Data
// docker run -d --name chroma-test -p 8000:8000 chromadb/chroma:0.6.4.dev226 for test
public class ChromaVectorStore implements VectorStoreBase {
    private final VectorStoreConfig config;
    private Client chromaClient;
    private Collection collection;
    private boolean isEmbedded;

    public static final String DEFAULT_EMBEDDING_FUNCTION = "default";
    public static final String OPENAI_EMBEDDING_FUNCTION = "openai";

    public ChromaVectorStore(VectorStoreConfig config) {
        this.config = config;
        this.isEmbedded = initializeClient();
        this.collection = createOrGetCollection();

        log.info("Chroma vector store initialized {} with collection: {}",
                isEmbedded ? "in embedded mode" : "with remote connection",
                config.getCollectionName());
    }

    private boolean initializeClient() {
        try {
            if (config.getHost() == null || "localhost".equals(config.getHost()) ||
                "127.0.0.1".equals(config.getHost()) || config.getHost().isEmpty()) {

                // For local embedded mode, use default URL
                chromaClient = new Client("http://localhost:8000");
                log.info("Initialized embedded Chroma client with default URL");
                return true;
            } else {
                String url = "http://" + config.getHost() + ":" + config.getPort();
                chromaClient = new Client(url);
                log.info("Initialized Chroma client for URL: {}", url);
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to initialize Chroma client: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Chroma client", e);
        }
    }

    private Collection createOrGetCollection() {
        try {
            String collectionName = config.getCollectionName();
            EmbeddingFunction ef = config.getEmbeddingFunction() != null && config.getEmbeddingFunction().equals(OPENAI_EMBEDDING_FUNCTION)
            ? new OpenAIEmbeddingFunction(WithParam.apiKey(config.getApiKey()), WithParam.model("text-embedding-3-small"), WithParam.baseAPI(config.getBaseUrl()))
            : new DefaultEmbeddingFunction();

            try {
                return chromaClient.getCollection(collectionName, ef);
            } catch (ApiException e) {
                if (e.getCode() == 404 || e.getCode() == 400) {
                    log.info("Collection {} not found, creating new one", collectionName);
                    Map<String, String> metadata = new HashMap<>();
                    metadata.put("hnsw:space", "cosine");
                    return chromaClient.createCollection(collectionName, metadata, true, ef);
                } else {
                    throw e;
                }
            }
        } catch (Exception e) {
            log.error("Failed to create/get collection {}: {}", config.getCollectionName(), e.getMessage());
            throw new RuntimeException("Failed to create/get collection", e);
        }
    }

    @Override
    public void insert(List<List<Double>> vectors, List<String> ids, List<Map<String, Object>> payloads) {
        try {
            List<Map<String, String>> metadatas = new ArrayList<>();
            List<String> documents = new ArrayList<>();

            for (int i = 0; i < payloads.size(); i++) {
                Map<String, Object> payload = payloads.get(i);
                Map<String, String> metadata = new HashMap<>();
                for (Map.Entry<String, Object> entry : payload.entrySet()) {
                    metadata.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "");
                }
                metadata.put("timestamp", String.valueOf(System.currentTimeMillis()));
                metadata.put("collection", config.getCollectionName());
                metadatas.add(metadata);

                String document = payload.get("memory") != null ? payload.get("memory").toString() : "";
                documents.add(document);
            }

            // Convert vectors to the format expected by ChromaDB
            List<Embedding> embeddings = new ArrayList<>();
            for (List<Double> vector : vectors) {
                embeddings.add(new Embedding(vector));
            }

            collection.add(
                embeddings,
                metadatas,
                documents,
                ids
            );

            log.info("Inserting {} vectors into collection {}", vectors.size(), config.getCollectionName());
        } catch (Exception e) {
            log.error("Failed to insert vectors: {}", e.getMessage());
            throw new RuntimeException("Failed to insert vectors", e);
        }
    }

    @Override
    public List<MemoryItem> search(String query, List<Double> vectors, int limit, Map<String, Object> filters) {
        try {
            Map<String, Object> whereClause = generateWhereClause(filters);

            Collection.QueryResponse response = collection.query(
                List.of(query),
                limit,
                whereClause,
                null,
                null
            );

            List<MemoryItem> results = new ArrayList<>();
            List<String> ids = response.getIds().get(0);
            List<List<Float>> distances = response.getDistances();
            List<Map<String, Object>> metadatas = response.getMetadatas().get(0);
            List<String> documents = response.getDocuments().get(0);

            for (int i = 0; i < ids.size(); i++) {
                String id = ids.get(i);
                double distance = distances.get(0).get(i).doubleValue();
                double similarity = 1.0 - distance;  // Convert distance to similarity
                Map<String, Object> metadata = metadatas.get(i);
                String document = documents.get(i);

                MemoryItem item = MemoryItem.builder()
                        .id(id)
                        .memory(document)
                        .score(similarity)
                        .userId(getStringFromMetadata(metadata, "user_id"))
                        .agentId(getStringFromMetadata(metadata, "agent_id"))
                        .runId(getStringFromMetadata(metadata, "run_id"))
                        .actorId(getStringFromMetadata(metadata, "actor_id"))
                        .role(getStringFromMetadata(metadata, "role"))
                        .metadata(metadata)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                results.add(item);
            }

            log.debug("Found {} similar vectors in Chroma collection {}", results.size(), config.getCollectionName());
            return results;
        } catch (Exception e) {
            log.error("Failed to search vectors: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private String getStringFromMetadata(Map<String, Object> metadata, String key) {
        Object value = metadata.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Generate a properly formatted where clause for ChromaDB.
     *
     * @param filters The filter conditions.
     * @return Properly formatted where clause for ChromaDB.
     */
    private Map<String, Object> generateWhereClause(Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return new HashMap<>();
        }

        // If only one filter is supplied, return it as is
        // (no need to wrap in $and based on chroma docs)
        if (filters.size() <= 1) {
            return new HashMap<>(filters);
        }

        List<Map<String, Object>> whereFilters = new ArrayList<>();
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            if (entry.getValue() instanceof String) {
                Map<String, Object> filter = new HashMap<>();
                filter.put(entry.getKey(), entry.getValue());
                whereFilters.add(filter);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("$and", whereFilters);
        return result;
    }

    /**
     * Parse a single item from GetResponse.
     *
     * @param response The GetResponse from ChromaDB.
     * @param index The index of the item to parse.
     * @return Parsed MemoryItem object.
     */
    private MemoryItem parseGetResponse(GetResult response, int index) {
        List<String> ids = response.getIds();
        List<Map<String, Object>> metadatas = response.getMetadatas();
        List<String> documents = response.getDocuments();

        if (index >= ids.size()) {
            return null;
        }

        String id = ids.get(index);
        Map<String, Object> metadata = index < metadatas.size() ? metadatas.get(index) : new HashMap<>();
        String document = index < documents.size() ? documents.get(index) : "";

        return MemoryItem.builder()
                .id(id)
                .memory(document)
                .score(1.0) // No similarity score available in get operation
                .userId(getStringFromMetadata(metadata, "user_id"))
                .agentId(getStringFromMetadata(metadata, "agent_id"))
                .runId(getStringFromMetadata(metadata, "run_id"))
                .actorId(getStringFromMetadata(metadata, "actor_id"))
                .role(getStringFromMetadata(metadata, "role"))
                .metadata(metadata)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Parse all items from GetResponse.
     *
     * @param response The GetResponse from ChromaDB.
     * @return List of parsed MemoryItem objects.
     */
    private List<MemoryItem> parseGetResponseList(GetResult response) {
        List<MemoryItem> results = new ArrayList<>();

        if (response == null || response.getIds() == null || response.getIds().isEmpty()) {
            return results;
        }

        List<String> ids = response.getIds();
        List<Map<String, Object>> metadatas = response.getMetadatas() != null ? response.getMetadatas() : new ArrayList<>();
        List<String> documents = response.getDocuments() != null ? response.getDocuments() : new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            Map<String, Object> metadata = i < metadatas.size() ? metadatas.get(i) : new HashMap<>();
            String document = i < documents.size() ? documents.get(i) : "";

            MemoryItem item = MemoryItem.builder()
                    .id(id)
                    .memory(document)
                    .score(1.0) // No similarity score available in list operation
                    .userId(getStringFromMetadata(metadata, "user_id"))
                    .agentId(getStringFromMetadata(metadata, "agent_id"))
                    .runId(getStringFromMetadata(metadata, "run_id"))
                    .actorId(getStringFromMetadata(metadata, "actor_id"))
                    .role(getStringFromMetadata(metadata, "role"))
                    .metadata(metadata)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            results.add(item);
        }

        log.debug("Listed {} vectors from Chroma collection {}", results.size(), config.getCollectionName());
        return results;
    }

    @Override
    public MemoryItem get(String vectorId) {
        try {
            GetResult result = collection.get((List.of(vectorId)), null, null);
            if (result != null && result.getIds() != null && !result.getIds().isEmpty()) {
                return parseGetResponse(result, 0);
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to get vector {}: {}", vectorId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<MemoryItem> list(Map<String, Object> filters, int limit) {
        try {
            Map<String, Object> whereClause = generateWhereClause(filters);
            GetResult result = collection.get(null, null, whereClause);
            return parseGetResponseList(result);
        } catch (Exception e) {
            log.error("Failed to list vectors: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void update(String vectorId, List<Double> vector, Map<String, Object> payload) {
        try {
            Map<String, Object> metadata = new HashMap<>(payload);
            metadata.put("timestamp", System.currentTimeMillis());
            metadata.put("collection", config.getCollectionName());

            collection.update(
                vectorId,
                metadata
            );

            log.debug("Updated vector {} in Chroma collection {}", vectorId, config.getCollectionName());
        } catch (Exception e) {
            log.error("Failed to update vector {}: {}", vectorId, e.getMessage());
            throw new RuntimeException("Failed to update vector", e);
        }
    }

    @Override
    public void delete(String vectorId) {
        try {
            collection.delete(Arrays.asList(vectorId), null, null);
            log.debug("Deleted vector {} from Chroma collection {}", vectorId, config.getCollectionName());
        } catch (Exception e) {
            log.error("Failed to delete vector {}: {}", vectorId, e.getMessage());
            throw new RuntimeException("Failed to delete vector", e);
        }
    }

    @Override
    public void deleteCol() {
        try {
            chromaClient.deleteCollection(config.getCollectionName());
            log.info("Deleted Chroma collection {}", config.getCollectionName());
        } catch (Exception e) {
            log.error("Failed to delete collection {}: {}", config.getCollectionName(), e.getMessage());
            throw new RuntimeException("Failed to delete collection", e);
        }
    }

    @Override
    public void reset() {
        try {
            deleteCol();
            collection = createOrGetCollection();
            log.info("Reset Chroma collection {}", config.getCollectionName());
        } catch (Exception e) {
            log.error("Failed to reset collection {}: {}", config.getCollectionName(), e.getMessage());
            throw new RuntimeException("Failed to reset collection", e);
        }
    }

    @Override
    public boolean collectionExists() {
        try {
            chromaClient.getCollection(config.getCollectionName(), null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void createCollection() {
        if (!collectionExists()) {
            collection = createOrGetCollection();
        }
    }

    @Override
    public long getVectorCount() {
        try {
            return collection.count();
        } catch (Exception e) {
            log.error("Failed to get vector count: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("vector_count", getVectorCount());
        stats.put("collection_name", config.getCollectionName());
        stats.put("provider", "chroma");
        stats.put("embedded_mode", isEmbedded);
        stats.put("dimensions", config.getEmbeddingModelDims());
        return stats;
    }

    @Override
    public void close() {
        try {
            if (chromaClient != null) {
                // ChromaDB Java client doesn't have a close method
                // Just null the reference to help with garbage collection
                chromaClient = null;
            }
            log.info("Closed Chroma vector store for collection {}", config.getCollectionName());
        } catch (Exception e) {
            log.warn("Error closing Chroma client: {}", e.getMessage());
        }
    }

    public static class OpenAIEmbeddingFunction implements EmbeddingFunction {
        public static final String DEFAULT_MODEL_NAME = "text-embedding-ada-002";
        public static final String DEFAULT_BASE_API = "https://api.openai.com/v1/embeddings";
        public static final String OPENAI_API_KEY_ENV = "OPENAI_API_KEY";
        private final OkHttpClient client = new OkHttpClient();
        private final Gson gson = new Gson();
        private final Map<String, Object> configParams = new HashMap<>();
        private static final List<WithParam> defaults = Arrays.asList(
                WithParam.baseAPI(DEFAULT_BASE_API),
                WithParam.defaultModel(DEFAULT_MODEL_NAME)
        );
    
    
        public OpenAIEmbeddingFunction() throws Exception {
            for (WithParam param : defaults) {
                param.apply(this.configParams);
            }
            WithParam.apiKeyFromEnv(OPENAI_API_KEY_ENV).apply(this.configParams);
        }
    
        public OpenAIEmbeddingFunction(WithParam... params) throws Exception {
            // apply defaults
    
            for (WithParam param : defaults) {
                param.apply(this.configParams);
            }
            for (WithParam param : params) {
                param.apply(this.configParams);
            }
        }
    
        public CreateEmbeddingResponse createEmbedding(CreateEmbeddingRequest req) throws EFException {
            Request request = new Request.Builder()
                    .url(this.configParams.get("baseAPI").toString())
                    .post(RequestBody.create(req.json(), JSON))
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + configParams.get("apiKey").toString())
                    .addHeader("X-Model-Provider-Id", "azure_openai")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
    
                String responseData = response.body().string();
    
                return gson.fromJson(responseData, CreateEmbeddingResponse.class);
            } catch (IOException e) {
                throw new EFException(e);
            }
        }
    
        @Override
        public Embedding embedQuery(String query) throws EFException {
            CreateEmbeddingRequest req = new CreateEmbeddingRequest().model(this.configParams.get("modelName").toString());
            req.input(new CreateEmbeddingRequest.Input(query));
            CreateEmbeddingResponse response = this.createEmbedding(req);
            return new Embedding(response.getData().get(0).getEmbedding());
        }
    
        @Override
        public List<Embedding> embedDocuments(List<String> documents) throws EFException {
            CreateEmbeddingRequest req = new CreateEmbeddingRequest().model(this.configParams.get("modelName").toString());
            req.input(new CreateEmbeddingRequest.Input(documents.toArray(new String[0])));
            CreateEmbeddingResponse response = this.createEmbedding(req);
            return response.getData().stream().map(emb -> new Embedding(emb.getEmbedding())).collect(Collectors.toList());
        }
    
        @Override
        public List<Embedding> embedDocuments(String[] documents) throws EFException {
            return embedDocuments(Arrays.asList(documents));
        }
    }
}