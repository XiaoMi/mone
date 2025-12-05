/*
 * Copyright 2024-2024 the original author or authors.
 */

package run.mone.hive.mcp.server.transport.streamable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for HttpTokenValidator to verify response parsing logic
 */
class HttpTokenValidatorTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testValidResponse_WithNameField() throws Exception {
        // Test the actual API response format
        String validJson = "{\"name\":\"wangzhidong1\",\"avatar\":null,\"description\":null,\"config\":{},\"configUpdateTime\":1722860114231}";
        JsonNode response = objectMapper.readTree(validJson);

        HttpTokenValidator validator = new HttpTokenValidator();
        boolean isValid = validator.isValidResponse(response);

        assertTrue(isValid, "Should be valid when name field exists and is not empty");
    }

    @Test
    void testInvalidResponse_EmptyName() throws Exception {
        String invalidJson = "{\"name\":\"\",\"avatar\":null}";
        JsonNode response = objectMapper.readTree(invalidJson);

        HttpTokenValidator validator = new HttpTokenValidator();
        boolean isValid = validator.isValidResponse(response);

        assertFalse(isValid, "Should be invalid when name is empty");
    }

    @Test
    void testInvalidResponse_NullName() throws Exception {
        String invalidJson = "{\"name\":null,\"avatar\":null}";
        JsonNode response = objectMapper.readTree(invalidJson);

        HttpTokenValidator validator = new HttpTokenValidator();
        boolean isValid = validator.isValidResponse(response);

        assertFalse(isValid, "Should be invalid when name is null");
    }

    @Test
    void testValidResponse_WithCodeField() throws Exception {
        String validJson = "{\"code\":0,\"data\":{\"user\":\"test\"}}";
        JsonNode response = objectMapper.readTree(validJson);

        HttpTokenValidator validator = new HttpTokenValidator();
        boolean isValid = validator.isValidResponse(response);

        assertTrue(isValid, "Should be valid when code is 0");
    }

    @Test
    void testInvalidResponse_WithNonZeroCode() throws Exception {
        String invalidJson = "{\"code\":1,\"message\":\"error\"}";
        JsonNode response = objectMapper.readTree(invalidJson);

        HttpTokenValidator validator = new HttpTokenValidator();
        boolean isValid = validator.isValidResponse(response);

        assertFalse(isValid, "Should be invalid when code is not 0");
    }

    @Test
    void testValidResponse_WithSuccessField() throws Exception {
        String validJson = "{\"success\":true,\"data\":{}}";
        JsonNode response = objectMapper.readTree(validJson);

        HttpTokenValidator validator = new HttpTokenValidator();
        boolean isValid = validator.isValidResponse(response);

        assertTrue(isValid, "Should be valid when success is true");
    }

    @Test
    void testInvalidResponse_WithSuccessFalse() throws Exception {
        String invalidJson = "{\"success\":false}";
        JsonNode response = objectMapper.readTree(invalidJson);

        HttpTokenValidator validator = new HttpTokenValidator();
        boolean isValid = validator.isValidResponse(response);

        assertFalse(isValid, "Should be invalid when success is false");
    }

    @Test
    void testValidResponse_EmptyObject() throws Exception {
        String emptyJson = "{}";
        JsonNode response = objectMapper.readTree(emptyJson);

        HttpTokenValidator validator = new HttpTokenValidator();
        boolean isValid = validator.isValidResponse(response);

        assertFalse(isValid, "Should be invalid for empty JSON object");
    }

    @Test
    void testValidate_EmptyEndpoint() {
        // Test with empty endpoint
        HttpTokenValidator validator = new HttpTokenValidator("");
        TokenValidator.ValidationResult result = validator.validate("test-token");

        assertFalse(result.isValid(), "Should be invalid when endpoint is empty");
    }

    @Test
    void testValidate_NullEndpoint() {
        // Test with null endpoint
        HttpTokenValidator validator = new HttpTokenValidator(null);
        TokenValidator.ValidationResult result = validator.validate("test-token");

        assertFalse(result.isValid(), "Should be invalid when endpoint is null");
    }

    @Test
    void testValidate_WhitespaceEndpoint() {
        // Test with whitespace endpoint
        HttpTokenValidator validator = new HttpTokenValidator("   ");
        TokenValidator.ValidationResult result = validator.validate("test-token");

        assertFalse(result.isValid(), "Should be invalid when endpoint is whitespace");
    }
}
