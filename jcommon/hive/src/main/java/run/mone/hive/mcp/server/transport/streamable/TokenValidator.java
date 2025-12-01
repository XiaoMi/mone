/*
 * Copyright 2024-2024 the original author or authors.
 */

package run.mone.hive.mcp.server.transport.streamable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface for validating bearer tokens.
 * Implementations should call an HTTP endpoint to validate tokens.
 */
public interface TokenValidator {

    /**
     * Validates a bearer token by calling an HTTP endpoint.
     * @param token The bearer token to validate
     * @return A validation result containing validity status and optional TTL
     */
    ValidationResult validate(String token);

    /**
     * Result of token validation.
     */
    class ValidationResult {
        private final boolean isValid;
        private final Duration ttl;
        private final Map<String, Object> userInfo;

        /**
         * Creates a validation result.
         * @param isValid Whether the token is valid
         * @param ttl The time-to-live for caching this result (null to use default)
         * @param userInfo User information extracted from token validation (null if none)
         */
        public ValidationResult(boolean isValid, Duration ttl, Map<String, Object> userInfo) {
            this.isValid = isValid;
            this.ttl = ttl;
            this.userInfo = userInfo != null ? new HashMap<>(userInfo) : new HashMap<>();
        }

        /**
         * Creates a validation result with default TTL and no user info.
         * @param isValid Whether the token is valid
         * @param ttl The time-to-live for caching this result (null to use default)
         */
        public ValidationResult(boolean isValid, Duration ttl) {
            this(isValid, ttl, null);
        }

        /**
         * Creates a validation result with default TTL.
         * @param isValid Whether the token is valid
         */
        public ValidationResult(boolean isValid) {
            this(isValid, null, null);
        }

        public boolean isValid() {
            return isValid;
        }

        public Duration getTtl() {
            return ttl;
        }

        public Map<String, Object> getUserInfo() {
            return userInfo != null ? new HashMap<>(userInfo) : new HashMap<>();
        }
    }
}
