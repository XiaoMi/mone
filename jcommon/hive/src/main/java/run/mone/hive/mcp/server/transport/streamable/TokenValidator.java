/*
 * Copyright 2024-2024 the original author or authors.
 */

package run.mone.hive.mcp.server.transport.streamable;

import java.time.Duration;

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

        /**
         * Creates a validation result.
         * @param isValid Whether the token is valid
         * @param ttl The time-to-live for caching this result (null to use default)
         */
        public ValidationResult(boolean isValid, Duration ttl) {
            this.isValid = isValid;
            this.ttl = ttl;
        }

        /**
         * Creates a validation result with default TTL.
         * @param isValid Whether the token is valid
         */
        public ValidationResult(boolean isValid) {
            this(isValid, null);
        }

        public boolean isValid() {
            return isValid;
        }

        public Duration getTtl() {
            return ttl;
        }
    }
}
