package run.mone.hive.mcp.core.client.transport.customizer;

import java.net.URI;
import java.net.http.HttpRequest;

import org.reactivestreams.Publisher;

import run.mone.hive.mcp.core.common.McpTransportContext;
import run.mone.hive.mcp.core.client.transport.customizer.McpAsyncHttpClientRequestCustomizer;

/**
 * Injects client auth headers (clientId, token) from the {@link McpTransportContext}
 * into outgoing HTTP requests.
 */
public class ClientAuthHeaderCustomizer implements McpAsyncHttpClientRequestCustomizer {

    public static final String HEADER_CLIENT_ID = "clientId";
    public static final String HEADER_TOKEN = "token";

    @Override
    public Publisher<HttpRequest.Builder> customize(HttpRequest.Builder builder, String method, URI endpoint,
            String body, McpTransportContext context) {
        Object clientId = context.get(HEADER_CLIENT_ID);
        Object token = context.get(HEADER_TOKEN);
        if (clientId != null) {
            builder.setHeader(HEADER_CLIENT_ID, String.valueOf(clientId));
        }
        if (token != null) {
            builder.setHeader(HEADER_TOKEN, String.valueOf(token));
        }
        return reactor.core.publisher.Mono.just(builder);
    }
}
