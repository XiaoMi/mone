/*
 * Copyright 2024-2024 the original author or authors.
 */

package run.mone.hive.mcp.server.transport.streamable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HTTP-based token validator that calls a remote endpoint to validate bearer tokens.
 * The endpoint URL is configured via environment variable: TOKEN_VALIDATION_ENDPOINT
 * Default: empty string (validation disabled)
 */
public class HttpTokenValidator implements TokenValidator {

    private static final Logger logger = LoggerFactory.getLogger(HttpTokenValidator.class);

    /**
     * Environment variable name for token validation endpoint
     */
    public static final String ENV_TOKEN_VALIDATION_ENDPOINT = "TOKEN_VALIDATION_ENDPOINT";

    private final String validationEndpoint;
    private final ObjectMapper objectMapper;
    private final int connectTimeout;
    private final int readTimeout;

    /**
     * Creates a new HttpTokenValidator with the default endpoint from environment variable.
     * Reads from TOKEN_VALIDATION_ENDPOINT env var, defaults to empty string if not set.
     */
    public HttpTokenValidator() {
        this(System.getenv(ENV_TOKEN_VALIDATION_ENDPOINT) != null ?
             System.getenv(ENV_TOKEN_VALIDATION_ENDPOINT) : "");
    }

    /**
     * Creates a new HttpTokenValidator with a custom endpoint.
     * @param validationEndpoint The HTTP endpoint for token validation (without query params)
     */
    public HttpTokenValidator(String validationEndpoint) {
        this(validationEndpoint, new ObjectMapper(), 5000, 5000);
    }

    /**
     * Creates a new HttpTokenValidator with custom configuration.
     * @param validationEndpoint The HTTP endpoint for token validation
     * @param objectMapper The ObjectMapper for parsing JSON responses
     * @param connectTimeout Connect timeout in milliseconds
     * @param readTimeout Read timeout in milliseconds
     */
    public HttpTokenValidator(String validationEndpoint, ObjectMapper objectMapper,
                             int connectTimeout, int readTimeout) {
        this.validationEndpoint = validationEndpoint;
        this.objectMapper = objectMapper;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public ValidationResult validate(String token) {
        // Check if validation endpoint is configured
        if (validationEndpoint == null || validationEndpoint.trim().isEmpty()) {
            logger.warn("Token validation endpoint is not configured (empty). Validation failed.");
            return new ValidationResult(false);
        }

        HttpURLConnection connection = null;
        try {
            // Build URL with token parameter
            String urlString = validationEndpoint + "?token=" + token;
            URL url = new URL(urlString);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Check if response has content
                int contentLength = connection.getContentLength();
                if (contentLength == 0) {
                    logger.warn("Token validation returned empty response (invalid token)");
                    return new ValidationResult(false);
                }

                // Read response
                String responseBody = new String(connection.getInputStream().readAllBytes());
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    logger.warn("Token validation returned empty response body (invalid token)");
                    return new ValidationResult(false);
                }

                // Parse JSON response
                JsonNode response = objectMapper.readTree(responseBody);

                // Check if response indicates success
                boolean isValid = isValidResponse(response);

                // Extract TTL from response if available
                Duration ttl = extractTtl(response);

                // Extract user info from response if available
                UserInfo userInfo = extractUserInfo(response);

                logger.debug("Token validation successful: valid={}, ttl={}, userInfo={}, response={}",
                    isValid, ttl, userInfo, responseBody);
                return new ValidationResult(isValid, ttl, userInfo);
            } else {
                logger.warn("Token validation failed with HTTP status: {}", responseCode);
                return new ValidationResult(false);
            }

        } catch (IOException e) {
            logger.error("Error calling token validation endpoint: {}", e.getMessage(), e);
            return new ValidationResult(false);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Determines if the API response indicates a valid token.
     * Override this method to customize validation logic based on your API response format.
     *
     * Default implementation for http://10.38.219.85/api/z-proxy/user endpoint:
     * - Valid token returns: {"name": "username", "avatar": ..., "config": {}, ...}
     * - Invalid token returns: HTTP error or empty/error response
     *
     * @param response The JSON response from the validation endpoint
     * @return true if the token is valid
     */
    protected boolean isValidResponse(JsonNode response) {
        // Check if response has "code" field (error response format)
        if (response.has("code")) {
            int code = response.get("code").asInt();
            // code == 0 typically means success
            if (code != 0) {
                return false;
            }
        }

        // Check if response has "success" field
        if (response.has("success")) {
            return response.get("success").asBoolean();
        }

        // For z-proxy/user endpoint: check if "name" field exists and is not null
        // This indicates a valid user was found for the token
        if (response.has("name")) {
            JsonNode nameNode = response.get("name");
            return !nameNode.isNull() && !nameNode.asText().isEmpty();
        }

        // Check if response has "data" field with user info
        if (response.has("data")) {
            JsonNode dataNode = response.get("data");
            // If data is not null and is an object, consider it valid
            return !dataNode.isNull() && (dataNode.isObject() || dataNode.isArray());
        }

        // If we got a 200 response with a non-empty JSON object, assume valid
        // This handles cases where the API returns user info directly
        return response.isObject() && response.size() > 0;
    }

    /**
     * Extracts TTL from the API response.
     * Override this method to extract custom TTL values from your API response.
     *
     * @param response The JSON response from the validation endpoint
     * @return The TTL duration, or null to use default
     */
    protected Duration extractTtl(JsonNode response) {
        // Default implementation: look for "ttl" or "expiresIn" field in seconds
        if (response.has("ttl")) {
            long ttlSeconds = response.get("ttl").asLong();
            return Duration.ofSeconds(ttlSeconds);
        }
        if (response.has("expiresIn")) {
            long expiresInSeconds = response.get("expiresIn").asLong();
            return Duration.ofSeconds(expiresInSeconds);
        }
        // Return null to use default TTL
        return null;
    }

    /**
     * Extracts user information from the API response.
     * Override this method to customize user info extraction based on your API response format.
     *
     * Default implementation for z-proxy/user endpoint format:
     * - Extracts common fields: name, userId, avatar, email, etc.
     * - Also supports nested "data" field
     *
     * @param response The JSON response from the validation endpoint
     * @return UserInfo containing user information, or empty UserInfo if none found
     */
    protected UserInfo extractUserInfo(JsonNode response) {
        UserInfo userInfo = new UserInfo();

        try {
            // Try to extract from root level first (z-proxy/user format)
            if (response.has("name") && !response.get("name").isNull()) {
                userInfo.setUsername(response.get("name").asText());
            }
            if (response.has("userId") && !response.get("userId").isNull()) {
                userInfo.setUserId(response.get("userId").asText());
            }

            // Extract from username field if name is not present
            if (response.has("username") && !response.get("username").isNull()) {
                userInfo.setUsername(response.get("username").asText());
            }

            // Extract clientId if present
            if (response.has("clientId") && !response.get("clientId").isNull()) {
                userInfo.setClientId(response.get("clientId").asText());
            }

            // If data field exists, extract from there
            if (response.has("data") && response.get("data").isObject()) {
                JsonNode dataNode = response.get("data");

                if (dataNode.has("userId") && !dataNode.get("userId").isNull()) {
                    userInfo.setUserId(dataNode.get("userId").asText());
                }
                if (dataNode.has("username") && !dataNode.get("username").isNull()) {
                    userInfo.setUsername(dataNode.get("username").asText());
                }
                if (dataNode.has("name") && !dataNode.get("name").isNull() && userInfo.getUsername() == null) {
                    userInfo.setUsername(dataNode.get("name").asText());
                }
                if (dataNode.has("clientId") && !dataNode.get("clientId").isNull()) {
                    userInfo.setClientId(dataNode.get("clientId").asText());
                }
            }

            logger.debug("Extracted user info: {}", userInfo);
        } catch (Exception e) {
            logger.warn("Failed to extract user info from response: {}", e.getMessage());
        }

        return userInfo;
    }
}
