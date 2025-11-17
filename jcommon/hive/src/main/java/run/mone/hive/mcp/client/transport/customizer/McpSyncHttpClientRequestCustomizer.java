/*
 * Originally from Spring AI MCP Java SDK
 * Adapted for custom MCP framework
 */

package run.mone.hive.mcp.client.transport.customizer;

import java.net.URI;
import java.net.http.HttpRequest;

import reactor.util.annotation.Nullable;
import run.mone.hive.mcp.client.transport.streamable.McpTransportContext;

/**
 * Customize {@link HttpRequest.Builder} before executing the request, in either SSE or
 * Streamable HTTP transport.
 * <p>
 * Synchronous customizer - use only in blocking contexts.
 *
 * @author Daniel Garnier-Moiroux
 */
public interface McpSyncHttpClientRequestCustomizer {

    void customize(HttpRequest.Builder builder, String method, URI endpoint, @Nullable String body,
                   McpTransportContext context);

    McpSyncHttpClientRequestCustomizer NOOP = new Noop();

    class Noop implements McpSyncHttpClientRequestCustomizer {

        @Override
        public void customize(HttpRequest.Builder builder, String method, URI endpoint, String body,
                              McpTransportContext context) {
            // No-op implementation
        }

    }

}
