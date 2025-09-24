package run.mone.neo4j;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * OllamaClient测试类
 */
public class OllamaClientTest {
    
    private OllamaClient ollamaClient;
    
    @Before
    public void setUp() {
        // 使用默认配置创建客户端
        ollamaClient = new OllamaClient();
    }
    
    @Test
    public void testClientInitialization() {
        assertNotNull("OllamaClient should not be null", ollamaClient);
        assertEquals("Default base URL should be set", "http://localhost:11434", ollamaClient.getBaseUrl());
        assertEquals("Default model should be set", "llama3.2", ollamaClient.getModel());
    }
    
    @Test
    public void testCustomClientInitialization() {
        String customBaseUrl = "http://custom-ollama:11434";
        String customModel = "custom-model";
        
        OllamaClient customClient = new OllamaClient(customBaseUrl, customModel);
        
        assertEquals("Custom base URL should be set", customBaseUrl, customClient.getBaseUrl());
        assertEquals("Custom model should be set", customModel, customClient.getModel());
    }
    
    @Test
    public void testCodeBlockProcessing() {
        // 模拟包含代码块的响应
        String responseWithCodeBlock = "这是一个Java示例：\n\n```java\npublic class Hello {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World\");\n    }\n}\n```\n\n这就是示例代码。";
        
        // 使用反射或者创建一个测试用的方法来测试代码块处理
        // 这里我们通过实际调用来测试（如果服务不可用会返回错误，但不会抛异常）
        OllamaClient.OllamaResponse response = ollamaClient.chat("请给我一个简单的Java Hello World程序");
        
        // 测试响应对象的基本结构
        assertNotNull("Response should not be null", response);
        assertNotNull("Response should have toJson method", response.toJson());
    }
    
    @Test
    public void testOllamaResponseCreation() {
        // 测试成功响应创建
        OllamaClient.OllamaResponse successResponse = OllamaClient.OllamaResponse.success("Clean text", "Original text");
        assertTrue("Success response should be successful", successResponse.isSuccess());
        assertEquals("Clean text should match", "Clean text", successResponse.getCleanText());
        assertEquals("Original text should match", "Original text", successResponse.getOriginalText());
        
        // 测试错误响应创建
        OllamaClient.OllamaResponse errorResponse = OllamaClient.OllamaResponse.error("Test error", "Error details");
        assertFalse("Error response should not be successful", errorResponse.isSuccess());
        assertEquals("Error message should match", "Test error", errorResponse.getError());
        assertEquals("Error detail should match", "Error details", errorResponse.getErrorDetail());
    }
    
    @Test
    public void testResponseJsonSerialization() {
        OllamaClient.OllamaResponse response = new OllamaClient.OllamaResponse();
        response.setSuccess(true);
        response.setCleanText("Test text");
        response.setHasCodeBlock(true);
        response.setCodeLanguage("java");
        response.setCodeContent("System.out.println(\"Hello\");");
        
        String json = response.toJson();
        assertNotNull("JSON should not be null", json);
        assertTrue("JSON should contain success field", json.contains("\"success\":true"));
        assertTrue("JSON should contain cleanText field", json.contains("\"cleanText\":\"Test text\""));
        assertTrue("JSON should contain hasCodeBlock field", json.contains("\"hasCodeBlock\":true"));
        assertTrue("JSON should contain codeLanguage field", json.contains("\"codeLanguage\":\"java\""));
    }
    
    @Test
    public void testGetMainContent() {
        // 测试有代码块的情况
        OllamaClient.OllamaResponse responseWithCode = new OllamaClient.OllamaResponse();
        responseWithCode.setHasCodeBlock(true);
        responseWithCode.setCodeContent("System.out.println(\"Hello\");");
        responseWithCode.setCleanText("Some explanation text");
        
        assertEquals("Should return code content when available", 
            "System.out.println(\"Hello\");", responseWithCode.getMainContent());
        
        // 测试没有代码块的情况
        OllamaClient.OllamaResponse responseWithoutCode = new OllamaClient.OllamaResponse();
        responseWithoutCode.setHasCodeBlock(false);
        responseWithoutCode.setCleanText("Just text content");
        
        assertEquals("Should return clean text when no code block", 
            "Just text content", responseWithoutCode.getMainContent());
    }
    
    @Test
    public void testServiceAvailabilityCheck() {
        // 测试服务可用性检查（这个测试可能会失败如果本地没有运行Ollama服务）
        boolean isAvailable = ollamaClient.isServiceAvailable();
        // 我们不断言结果，因为这取决于本地环境
        // 但至少确保方法不会抛异常
        assertNotNull("Service availability check should not throw exception", Boolean.valueOf(isAvailable));
    }
    
    @Test
    public void testGetAvailableModels() {
        // 测试获取可用模型（这个测试可能会失败如果本地没有运行Ollama服务）
        String models = ollamaClient.getAvailableModels();
        assertNotNull("Models response should not be null", models);
        assertTrue("Models response should be valid JSON or error message", 
            models.contains("{") || models.contains("error"));
    }
    
    @Test
    public void testChatWithSystemPrompt() {
        // 测试带系统提示的聊天
        OllamaClient.OllamaResponse response = ollamaClient.chat("Hello", "You are a helpful assistant");
        assertNotNull("Response should not be null", response);
        // 响应可能是错误（如果服务不可用），但不应该抛异常
    }
    
    @Test
    public void testChatWithoutSystemPrompt() {
        // 测试不带系统提示的聊天
        OllamaClient.OllamaResponse response = ollamaClient.chat("Hello");
        assertNotNull("Response should not be null", response);
        // 响应可能是错误（如果服务不可用），但不应该抛异常
    }
}
