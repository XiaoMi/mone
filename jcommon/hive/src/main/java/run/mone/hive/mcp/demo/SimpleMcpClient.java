package run.mone.hive.mcp.demo;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import reactor.core.publisher.Flux;
import run.mone.hive.mcp.client.McpClient;
import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.client.transport.HttpClientSseClientTransport;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.mcp.client.transport.StdioClientTransport;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema.CallToolRequest;
import run.mone.hive.mcp.spec.McpSchema.CallToolResult;
import run.mone.hive.mcp.spec.McpSchema.ClientCapabilities;
import run.mone.hive.mcp.spec.McpSchema.ListToolsResult;

public class SimpleMcpClient {

    public static void main(String[] args) {
        // demo for stdio client
        // new SimpleMcpClient().simpleClientViaStdio();

        // demo for sse client with stream tool call
        try {
            new SimpleMcpClient().simpleClientViaSSE();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void simpleClientViaSSE() throws InterruptedException {
        // Create a sync client with custom configuration, using sse transport
        ClientMcpTransport transport = new HttpClientSseClientTransport("http://localhost:8080");
        try (McpSyncClient client = McpClient.using(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(ClientCapabilities.builder()
                        .roots(true) // Enable roots capability
                        .build())
                .sync()) {
            client.initialize();
            ListToolsResult listTools = client.listTools();
            System.out.println("listTools: " + listTools);

            // Call a tool, 
            callToolCalculator(client); // call calculator tool
            // callToolWriter(client); // call writer tool

            TimeUnit.SECONDS.sleep(30);
        }
    }

    private void callToolCalculator(McpSyncClient client) {
        Flux<CallToolResult> result = client.callToolStream(
            new CallToolRequest("calculator",
            Map.of("operation", "add", "a", 2, "b", 3))
            );

        // result.subscribe(System.out::println);
        result.subscribe(new Subscriber<CallToolResult>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(CallToolResult t) {
                System.out.println("onNext: " + t);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError: " + t);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }

    private void callToolWriter(McpSyncClient client) {
        String article = """
                雨夜的玻璃窗上爬满水痕，林夏缩在沙发里盯着墙上的挂钟——凌晨1点17分，距离丈夫周明失踪已整整72小时。突然，门铃刺破寂静，她赤脚冲到玄关，门外空无一人，唯有一个湿漉漉的牛皮纸箱静默地躺着。
                纸箱没有寄件人信息，封口处用麻绳系着一枚褪色的铜钥匙。她颤抖着手拆开，最先滑出的是一张泛黄的照片：1998年的老城巷口，8岁的她正蹲在地上系鞋带，身后阴影里站着一名戴鸭舌帽的男人，左手无名指缺了一截。照片背面用红笔潦草地写着：「第三个。」
                记忆轰然裂开一道缝隙。三天前，周明在电话里也提过「第三个」，声音嘶哑得像是被砂纸碾过：「钥匙在……第三个抽屉……」话音未落便被刺耳的撞击声切断，再无人应答。
                此刻，钥匙齿痕与家中第三个抽屉的锁孔完美契合。林夏屏住呼吸转动钥匙——咔嗒。
                抽屉里只有一本皮革封面的日记，扉页夹着一片干枯的银杏叶，叶脉间印着暗褐色的指纹。第一页的日期是1976年5月12日，字迹工整得诡异：
                「今天，我杀死了那个总在巷口徘徊的男人。
                但我知道，他还会回来。」
                窗外雷声炸响，闪电将玻璃映得惨白。林夏猛地回头——照片里缺了无名指的男人，此刻正倒映在雨幕中的窗玻璃上，嘴角缓缓咧至耳根。
                """;
        ;

        Flux<CallToolResult> res = client.callToolStream(new CallToolRequest("writerOperation",
                Map.of("operation", "expandArticle", "article", article)));

        res.subscribe(new Subscriber<CallToolResult>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(CallToolResult t) {
                System.out.println("onNext: " + t);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError: " + t);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }

    public void simpleClientViaStdio() {
        // Create a sync client with custom configuration, using stdio transport
        ServerParameters params = ServerParameters.builder("docker")
                .args("run", "-i", "--rm", "mcp/fetch", "--ignore-robots-txt")
                .build();
        ClientMcpTransport transport = new StdioClientTransport(params);
        ListToolsResult listTools;
        try (McpSyncClient client = McpClient.using(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(ClientCapabilities.builder()
                        .roots(true) // Enable roots capability
                        .build())
                .sync()) {
            client.initialize();
            listTools = client.listTools();
        }
        System.out.println("listTools: " + listTools);
    }
}
