package run.mone.mcp.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.elasticsearch.function.ElasticsearchFunction;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ElasticsearchMcpApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test1() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
        Map<String, Object> args = new HashMap<>();
        args.put("index", "users");
        args.put("id", "1");
        args.put("operation", "get");
        GetRequest request = new GetRequest((String) args.get("index"), (String) args.get("id"));
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println("+++++");
        System.out.println(response.getSourceAsString());
    }

    //查询指定id与index的数据
    @Test
    void test2() {
        ElasticsearchFunction elasticsearchFunction = new ElasticsearchFunction();
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "get");
        args.put("index", "users");
        args.put("id", "1");
        McpSchema.CallToolResult apply = elasticsearchFunction.apply(args);
        apply.content().forEach(System.out::println);
    }

    //查询所有数据
    @Test
    void test3() {
        ElasticsearchFunction elasticsearchFunction = new ElasticsearchFunction();
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "search"); // 指定操作为 search
        args.put("index", "users222"); //
        args.put("query", Collections.singletonMap("match_all", Collections.emptyMap()));
        McpSchema.CallToolResult apply = elasticsearchFunction.apply(args);
        apply.content().forEach(System.out::println);
    }

    //插入数据
    @Test
    void test4() {
        ElasticsearchFunction elasticsearchFunction = new ElasticsearchFunction();
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "index");
        args.put("index", "users222");
        args.put("id", "5");
        Map<String, Object> document = new HashMap<>();
        document.put("name", "John Doe");
        document.put("age", "30");
        document.put("city", "New York");
        args.put("document", document);
        McpSchema.CallToolResult apply = elasticsearchFunction.apply(args);
        apply.content().forEach(System.out::println);
    }


    @Test
    void test5() {
        ElasticsearchFunction function = new ElasticsearchFunction();
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "update");
        args.put("index", "users");
        args.put("id", "1");
        args.put("document", Map.of("age", 35));
        McpSchema.CallToolResult apply = function.apply(args);
        apply.content().forEach(System.out::println);

    }

    @Test
    void test6() {
        ElasticsearchFunction function = new ElasticsearchFunction();

        Map<String, Object> args = new HashMap<>();
        args.put("operation", "delete");
        args.put("index", "users");
        args.put("id", "4");
        McpSchema.CallToolResult apply = function.apply(args);
        apply.content().forEach(System.out::println);
    }

    @Test
    void test7() {
        ElasticsearchFunction function = new ElasticsearchFunction();
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "createIndex");
        args.put("index", "new_users");
        args.put("mappings", Map.of(
                "properties", Map.of(
                        "name", Map.of("type", "text"),
                        "age", Map.of("type", "integer")
                )
        ));
        McpSchema.CallToolResult apply = function.apply(args);
        apply.content().forEach(System.out::println);
    }


}





