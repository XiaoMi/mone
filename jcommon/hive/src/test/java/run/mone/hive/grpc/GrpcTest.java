package run.mone.hive.grpc;

import com.google.api.client.util.Lists;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.grpc.demo.SimpleMcpGrpcServer;
import run.mone.hive.mcp.grpc.transport.GrpcClientTransport;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author goodjava@qq.com
 * @date 2025/4/2 09:29
 */
public class GrpcTest {


    @SneakyThrows
    @Test
    public void testServer() {
        SimpleMcpGrpcServer server = new SimpleMcpGrpcServer(null, new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
        server.addTool(new McpServer.ToolRegistration(null, (a) -> {
            return new McpSchema.CallToolResult(Lists.newArrayList(), false);
        }));
        server.init();
        System.in.read();
    }


    @Test
    public void testClient() {
        GrpcClientTransport client = new GrpcClientTransport("127.0.0.1", SimpleMcpGrpcServer.GRPC_PORT);
        client.connect(a -> {
            return null;
        });
        Map<String,String> params = new HashMap<>();
        McpSchema.JSONRPCRequest req = new McpSchema.JSONRPCRequest("", "tools/call", null, params);
        client.sendMessage(req);

    }

}
