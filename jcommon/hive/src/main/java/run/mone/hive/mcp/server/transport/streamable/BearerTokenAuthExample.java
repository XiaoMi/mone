/*
 * Copyright 2024-2024 the original author or authors.
 */

package run.mone.hive.mcp.server.transport.streamable;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;

/**
 * Example usage of bearer token authentication in HttpServletStreamableServerTransport.
 *
 * This example shows how to:
 * 1. Enable bearer token authentication using environment variable configuration
 * 2. Configure custom token cache TTL
 * 3. Use a custom token validator with custom validation logic
 *
 * IMPORTANT: Set environment variable before starting the application:
 * export TOKEN_VALIDATION_ENDPOINT=http://10.38.219.85/api/z-proxy/user
 */
public class BearerTokenAuthExample {

    /**
     * Example 1: Using default HttpTokenValidator (reads from TOKEN_VALIDATION_ENDPOINT env var)
     *
     * Before running, set environment variable:
     * export TOKEN_VALIDATION_ENDPOINT=http://10.38.219.85/api/z-proxy/user
     *
     * If not set, endpoint defaults to empty string and validation will fail.
     */
    public static HttpServletStreamableServerTransport createWithDefaultValidator() {
        return HttpServletStreamableServerTransport.builder()
                .objectMapper(new ObjectMapper())
                .mcpEndpoint("/mcp")
                .tokenValidator(new HttpTokenValidator()) // Reads from TOKEN_VALIDATION_ENDPOINT
                .tokenCacheTtl(Duration.ofMinutes(5)) // Cache tokens for 5 minutes
                .build();
    }

    /**
     * Example 2: Using custom validation endpoint
     */
    public static HttpServletStreamableServerTransport createWithCustomEndpoint() {
        String customEndpoint = "http://your-domain.com/api/validate-token";
        return HttpServletStreamableServerTransport.builder()
                .objectMapper(new ObjectMapper())
                .mcpEndpoint("/mcp")
                .tokenValidator(new HttpTokenValidator(customEndpoint))
                .tokenCacheTtl(Duration.ofMinutes(10)) // Cache for 10 minutes
                .build();
    }

    /**
     * Example 3: Custom token validator with custom validation logic
     */
    public static HttpServletStreamableServerTransport createWithCustomValidator() {
        TokenValidator customValidator = new HttpTokenValidator() {
            @Override
            protected boolean isValidResponse(com.fasterxml.jackson.databind.JsonNode response) {
                // Custom validation logic based on your API response format
                // Example: Check if "code" field equals 0 and "data" is not null
                if (response.has("code") && response.get("code").asInt() == 0) {
                    return response.has("data") && !response.get("data").isNull();
                }
                return false;
            }

            @Override
            protected Duration extractTtl(com.fasterxml.jackson.databind.JsonNode response) {
                // Extract custom TTL from response
                // Example: If your API returns "expiresIn" in seconds
                if (response.has("data") && response.get("data").has("expiresIn")) {
                    long expiresIn = response.get("data").get("expiresIn").asLong();
                    return Duration.ofSeconds(expiresIn);
                }
                // Return null to use default TTL
                return null;
            }
        };

        return HttpServletStreamableServerTransport.builder()
                .objectMapper(new ObjectMapper())
                .mcpEndpoint("/mcp")
                .tokenValidator(customValidator)
                .tokenCacheTtl(Duration.ofMinutes(5))
                .build();
    }

    /**
     * Example 4: Disabling bearer token auth (using legacy auth function)
     */
    public static HttpServletStreamableServerTransport createWithoutBearerAuth() {
        HttpServletStreamableServerTransport transport = HttpServletStreamableServerTransport.builder()
                .objectMapper(new ObjectMapper())
                .mcpEndpoint("/mcp")
                // No tokenValidator configured - bearer auth disabled
                .build();

        // You can still use the legacy auth function
        transport.setAuthFunction(sessionId -> {
            // Your custom session-based auth logic
            return true;
        });

        return transport;
    }

    /**
     * Example 5: Combining bearer token auth with legacy auth function
     */
    public static HttpServletStreamableServerTransport createWithBothAuthMethods() {
        HttpServletStreamableServerTransport transport = HttpServletStreamableServerTransport.builder()
                .objectMapper(new ObjectMapper())
                .mcpEndpoint("/mcp")
                .tokenValidator(new HttpTokenValidator()) // Enable bearer token auth
                .tokenCacheTtl(Duration.ofMinutes(5))
                .build();

        // Also set legacy auth function for additional checks
        transport.setAuthFunction(sessionId -> {
            // Additional session-based validation
            return sessionId != null && !sessionId.isEmpty();
        });

        return transport;
    }

    /**
     * Environment variable configuration:
     *
     * Set TOKEN_VALIDATION_ENDPOINT before starting the application:
     *
     * Linux/macOS:
     * export TOKEN_VALIDATION_ENDPOINT=http://10.38.219.85/api/z-proxy/user
     *
     * Windows:
     * set TOKEN_VALIDATION_ENDPOINT=http://10.38.219.85/api/z-proxy/user
     *
     * Docker:
     * docker run -e TOKEN_VALIDATION_ENDPOINT=http://10.38.219.85/api/z-proxy/user ...
     *
     * Kubernetes:
     * env:
     *   - name: TOKEN_VALIDATION_ENDPOINT
     *     value: "http://10.38.219.85/api/z-proxy/user"
     *
     * If TOKEN_VALIDATION_ENDPOINT is not set or empty:
     * - HttpTokenValidator will return validation failure for all tokens
     * - Log warning: "Token validation endpoint is not configured (empty). Validation failed."
     */

    /**
     * Client usage example:
     *
     * When making requests to the MCP server with bearer token auth enabled,
     * clients can provide the token in two ways:
     *
     * Method 1: Authorization Header (Recommended)
     * ============================================
     * GET /mcp HTTP/1.1
     * Host: your-server.com
     * Accept: text/event-stream
     * Authorization: Bearer 59260c1b-16a4-4bed-9cbf-e5bd709af6a7
     * mcp-client-id: client-session-id
     *
     * Method 2: URL Query Parameter
     * =============================
     * GET /mcp?token=59260c1b-16a4-4bed-9cbf-e5bd709af6a7 HTTP/1.1
     * Host: your-server.com
     * Accept: text/event-stream
     * mcp-client-id: client-session-id
     *
     * POST /mcp?token=59260c1b-16a4-4bed-9cbf-e5bd709af6a7 HTTP/1.1
     * Host: your-server.com
     * Content-Type: application/json
     *
     * {"jsonrpc": "2.0", "method": "tools/call", ...}
     *
     * Priority:
     * - Authorization header is checked first
     * - If not found, then URL parameter is checked
     *
     * The server will:
     * 1. Check if TOKEN_VALIDATION_ENDPOINT is configured (not empty)
     * 2. Extract the token from Authorization header or URL parameter
     * 3. Check the cache for validation result
     * 4. If not cached, call: ${TOKEN_VALIDATION_ENDPOINT}?token=59260c1b-16a4-4bed-9cbf-e5bd709af6a7
     * 5. Cache the result for the configured TTL (default 5 minutes)
     * 6. Allow or deny the request based on validation result
     *
     * Note: If TOKEN_VALIDATION_ENDPOINT is not set or empty, all token validations will fail.
     */
}
