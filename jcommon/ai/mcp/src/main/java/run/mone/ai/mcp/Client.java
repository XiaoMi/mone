package run.mone.ai.mcp;

import io.modelcontextprotocol.kotlin.sdk.Implementation;
import io.modelcontextprotocol.kotlin.sdk.client.ClientOptions;
import io.modelcontextprotocol.kotlin.sdk.client.StdioClientTransport;
import io.modelcontextprotocol.kotlin.sdk.shared.Transport;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Client {
    private Process process;
    private final io.modelcontextprotocol.kotlin.sdk.client.Client inner;

    public Client(ServerConfig config, String name, String version) throws IOException, InterruptedException {
        Transport transport = StringUtils.isEmpty(config.getUrl()) ?
                initCommand(config) :
                initURL(config);
        inner = new io.modelcontextprotocol.kotlin.sdk.client.Client(
                new Implementation(StringUtils.isEmpty(name) ? "example-client" : name, StringUtils.isEmpty(version) ? "1.0.0" : version),
                new ClientOptions()
        );
        kotlinx.coroutines.BuildersKt.runBlocking(
                kotlinx.coroutines.Dispatchers.getIO(),
                (scope, continuation) -> {
                    return inner.connect(transport, continuation);
                }
        );
    }

    private Transport initCommand(ServerConfig config) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> t = new ArrayList<>();
        t.add(config.command);
        t.addAll(config.args);
        processBuilder.command(t.toArray(new String[0]));
        process = processBuilder.start();

        return new StdioClientTransport(
                process.getInputStream(),
                process.getOutputStream()
        );
    }

    private Transport initURL(ServerConfig config) {
        // todo
        return null;
    }

    public void destroy() {
        if (process != null) {
            process.destroy();
        }
    }

    public io.modelcontextprotocol.kotlin.sdk.CallToolResultBase callTool(String name, Map<String, Object> arguments, boolean compatibility, io.modelcontextprotocol.kotlin.sdk.shared.RequestOptions options) throws InterruptedException {
        return kotlinx.coroutines.BuildersKt.runBlocking(
                kotlinx.coroutines.Dispatchers.getIO(),
                (scope, continuation) -> {
                    return inner.callTool(name, arguments, compatibility, options, continuation);
                }
        );
    }

    public io.modelcontextprotocol.kotlin.sdk.ListToolsResult listTools(io.modelcontextprotocol.kotlin.sdk.ListToolsRequest request, io.modelcontextprotocol.kotlin.sdk.shared.RequestOptions options) throws InterruptedException {
        return kotlinx.coroutines.BuildersKt.runBlocking(
                kotlinx.coroutines.Dispatchers.getIO(),
                (scope, continuation) -> {
                    return inner.listTools(request, options, continuation);
                }
        );
    }

    public io.modelcontextprotocol.kotlin.sdk.ListResourcesResult listResources(io.modelcontextprotocol.kotlin.sdk.ListResourcesRequest request, io.modelcontextprotocol.kotlin.sdk.shared.RequestOptions options) throws InterruptedException {
        return kotlinx.coroutines.BuildersKt.runBlocking(
                kotlinx.coroutines.Dispatchers.getIO(),
                (scope, continuation) -> {
                    return inner.listResources(request, options, continuation);
                }
        );
    }

    public io.modelcontextprotocol.kotlin.sdk.ListResourceTemplatesResult listResourceTemplates(io.modelcontextprotocol.kotlin.sdk.ListResourceTemplatesRequest request, io.modelcontextprotocol.kotlin.sdk.shared.RequestOptions options) throws InterruptedException {
        return kotlinx.coroutines.BuildersKt.runBlocking(
                kotlinx.coroutines.Dispatchers.getIO(),
                (scope, continuation) -> {
                    return inner.listResourceTemplates(request, options, continuation);
                }
        );
    }

    public io.modelcontextprotocol.kotlin.sdk.ReadResourceResult readResource(io.modelcontextprotocol.kotlin.sdk.ReadResourceRequest request, io.modelcontextprotocol.kotlin.sdk.shared.RequestOptions options) throws InterruptedException {
        return kotlinx.coroutines.BuildersKt.runBlocking(
                kotlinx.coroutines.Dispatchers.getIO(),
                (scope, continuation) -> {
                    return inner.readResource(request, options, continuation);
                }
        );
    }
}
