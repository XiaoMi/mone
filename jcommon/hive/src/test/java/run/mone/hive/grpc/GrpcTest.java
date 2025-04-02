package run.mone.hive.grpc;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.client.McpClient;
import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.grpc.demo.SimpleMcpGrpcServer;
import run.mone.hive.mcp.grpc.transport.GrpcClientTransport;
import run.mone.hive.mcp.hub.McpConfig;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author goodjava@qq.com
 * @date 2025/4/2 09:29
 */
public class GrpcTest {


    @SneakyThrows
    @Test
    public void testServer() {
        CopyOnWriteArrayList<McpServer.ToolRegistration> tools = new CopyOnWriteArrayList<>();

        tools.add(new McpServer.ToolRegistration(new McpSchema.Tool("a", "a", "{}"), (a) -> {
            McpSchema.TextContent tc = new McpSchema.TextContent("a","data:data");
            return new McpSchema.CallToolResult(com.google.common.collect.Lists.newArrayList(tc), false);
        }));

        CopyOnWriteArrayList<McpServer.ToolStreamRegistration> streamTools = new CopyOnWriteArrayList<>();
        streamTools.add(new McpServer.ToolStreamRegistration(new McpSchema.Tool("s","s","{}"),(a)-> Flux.create(sink->{
            McpSchema.TextContent tc = new McpSchema.TextContent("stream","data:data");
            sink.next(new McpSchema.CallToolResult(Lists.newArrayList(tc),false));
            sink.next(new McpSchema.CallToolResult(Lists.newArrayList(tc),false));
            sink.complete();
        })));

        SimpleMcpGrpcServer server = new SimpleMcpGrpcServer(null, tools, streamTools);
        server.init();
        System.in.read();
    }


    @Test
    public void testClientTransport() {
        GrpcClientTransport client = new GrpcClientTransport("127.0.0.1", SimpleMcpGrpcServer.GRPC_PORT);
        client.connect(a -> null).subscribe();

        McpSchema.CallToolRequest r = new McpSchema.CallToolRequest("a", ImmutableMap.of("k", "v", "k1", "v1"));
        //tools/call
        McpSchema.JSONRPCRequest req = new McpSchema.JSONRPCRequest("", "a", UUID.randomUUID().toString(), r, "");
        client.sendMessage(req).subscribe(System.out::println);

    }

    @SneakyThrows
    @Test
    public void testStreamClientTransport() {
        GrpcClientTransport client = new GrpcClientTransport("127.0.0.1", SimpleMcpGrpcServer.GRPC_PORT);
        client.connect(a -> null).subscribe();

        McpSchema.CallToolRequest r = new McpSchema.CallToolRequest("s", ImmutableMap.of("sk", "v", "sk1", "v1"));
        //tools/call
        McpSchema.JSONRPCRequest req = new McpSchema.JSONRPCRequest("", "s", UUID.randomUUID().toString(), r, "");
        client.sendStreamMessage(req).subscribe(System.out::println);

        System.in.read();

    }

    @Test
    public void testClient() {
        McpConfig.ins().setClientId("1212");
        GrpcClientTransport client = new GrpcClientTransport("127.0.0.1", SimpleMcpGrpcServer.GRPC_PORT);
        McpSyncClient mc = McpClient.using(client).sync();
        McpSchema.CallToolRequest req = new McpSchema.CallToolRequest("a", ImmutableMap.of("k", "v", "k1", "v1"));
        McpSchema.CallToolResult res = mc.callTool(req);
        System.out.println(res);
    }

    @SneakyThrows
    @Test
    public void testStreamClient() {
        McpConfig.ins().setClientId("1212");
        GrpcClientTransport client = new GrpcClientTransport("127.0.0.1", SimpleMcpGrpcServer.GRPC_PORT);
        McpSyncClient mc = McpClient.using(client).sync();
        McpSchema.CallToolRequest req = new McpSchema.CallToolRequest("s", ImmutableMap.of("k", "v", "k1", "v1"));
        mc.callToolStream(req).subscribe(System.out::println);
        System.in.read();
    }

    @Test
    public void testPing() {
        McpConfig.ins().setClientId("1212");
        GrpcClientTransport client = new GrpcClientTransport("127.0.0.1", SimpleMcpGrpcServer.GRPC_PORT);
        McpSyncClient mc = McpClient.using(client).sync();
        Object res = mc.ping();
        System.out.println(res);
    }

}
