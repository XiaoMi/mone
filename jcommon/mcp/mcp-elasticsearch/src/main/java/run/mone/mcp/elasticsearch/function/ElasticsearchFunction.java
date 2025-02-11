package run.mone.mcp.elasticsearch.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class ElasticsearchFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "elasticsearchOperation";

    private String desc = "Elasticsearch operations including index management, document CRUD, and search queries";

    private String toolScheme = """
            {
            "type": "object",
            "properties": {
                "operation": {
                    "type": "string",
                    "enum": ["index", "get", "search", "update", "delete", "createIndex", "deleteIndex", "existsIndex"],
                    "description": "The operation to perform on Elasticsearch"
                },
                "index": {
                    "type": "string",
                    "description": "The index name to operate on"
                },
                "id": {
                     "type": "string",
                     "description": "Document ID"
                },
                "document": {
                      "type": "object",
                      "description": "Document content for indexing/updating"
                },
                 "query": {
                       "type": "object",
                       "description": "Search query DSL"
                },
                "mappings": {
                        "type": "object",
                        "description": "Index mappings definition"
                }
            },
            "required": ["operation", "index"]
    }
    """;

    private RestHighLevelClient client;

    public ElasticsearchFunction(){
        this.client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String operation = (String) args.get("operation");
        String index = (String) args.get("index");

        try{
            String res = switch (operation){
                case "index" -> handleIndexRequest(args);
                case "get" -> handleGetRequest(args);
                case "search" -> handleSearchRequest(args);
                case "update" -> handleUpdateRequest(args);
                case "delete" -> handleDeleteRequest(args);
                case "createIndex" -> handleCreateIndex(args);
                case "deleteIndex" -> handleDeleteIndex(index);
                case "existsIndex" -> handleExistsIndex(index);
                default -> "no this operation";
            };
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false);
        } catch (IOException e) {
            log.error("Error performing Elasticsearch operation: ", e);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }finally{
            closeClient();
        }
    }

    private void closeClient() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                log.error("Failed to close Elasticsearch client: ", e);
            }
        }
    }

    private String handleIndexRequest(Map<String, Object> args) throws IOException {
        IndexRequest request = new IndexRequest((String) args.get("index"));
        if(args.containsKey("id")){
            request.id((String) args.get("id"));
        }
        request.source((Map<String, Object>) args.get("document"), XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.toString();
    }

    private String handleGetRequest(Map<String, Object> args) throws IOException {
        GetRequest request = new GetRequest((String) args.get("index"), (String) args.get("id"));
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        return response.getSourceAsString();
    }

    private String handleSearchRequest(Map<String, Object> args) throws IOException {
        SearchRequest request = new SearchRequest((String) args.get("index"));
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //构建查询DSL
        if(args.containsKey("query")){
            QueryBuilder queryBuilder = buildQueryBuilder((Map<String, Object>) args.get("query"));
            sourceBuilder.query(queryBuilder);
        }
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        return processSearchResults(response);
    }

    private QueryBuilder buildQueryBuilder(Map<String, Object> query) {
        if (query.containsKey("match_all")){
            return QueryBuilders.matchAllQuery();
        }else if (query.containsKey("field") && query.containsKey("value")){
            String field = (String) query.get("field");
            String value = (String) query.get("value");
            return QueryBuilders.matchQuery(field, value);
        }else {
            throw new IllegalArgumentException("Unsupported query format. Expected 'match_all' or 'field' and 'value'.");
        }

    }

    private String processSearchResults(SearchResponse response) {
        StringBuilder sb = new StringBuilder();
        for (SearchHit hit : response.getHits().getHits()) {
            sb.append("ID: ").append(hit.getId())
                    .append(" Score: ").append(hit.getScore())
                    .append(" Source: ").append(hit.getSourceAsString())
                    .append("\n");
        }
        return sb.toString();
    }

    private String handleUpdateRequest(Map<String, Object> args) throws IOException {
        UpdateRequest request = new UpdateRequest((String) args.get("index"), (String) args.get("id")
        ).doc((Map<String, Object>) args.get("document"), XContentType.JSON);

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        return response.toString();
    }

    private String handleDeleteRequest(Map<String, Object> args) throws IOException {
        DeleteRequest request  = new DeleteRequest((String) args.get("index"), (String) args.get("id"));
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        return response.toString();
    }

    private String handleCreateIndex(Map<String, Object> args) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest((String) args.get("index"));
        if (args.containsKey("mappings")) {
            request.mapping((Map<String, Object>) args.get("mappings"));
        }
        client.indices().create(request, RequestOptions.DEFAULT);
        return "Index created successfully";
    }

    private String handleDeleteIndex(String index) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        client.indices().delete(request, RequestOptions.DEFAULT);
        return "Index deleted successfully";
    }

    private String handleExistsIndex(String index) throws IOException {
        boolean exists = client.indices().exists(
                new GetIndexRequest(index), RequestOptions.DEFAULT
        );
        return "Index exists: " + exists;
    }



}
