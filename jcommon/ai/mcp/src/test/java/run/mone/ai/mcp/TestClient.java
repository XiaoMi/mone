package run.mone.ai.mcp;

import io.modelcontextprotocol.kotlin.sdk.ListToolsRequest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestClient {
    @Test
    public void testTool() throws IOException, InterruptedException {
        ServerConfig config = ServerConfig.builder().command("docker").args(List.of("run",
                "-i",
                "--rm",
                "mcp/fetch",
                "--ignore-robots-txt")).build();
        Client client = new Client(config, null, null);
        var res = client.listTools(new ListToolsRequest(), null);
        System.out.println(res.getTools());
        var res2 = client.callTool("fetch", Map.of("url", "https://github.com/modelcontextprotocol/servers"), false, null);
        System.out.println(res2.getContent());
    }
}
