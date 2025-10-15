package run.mone.neo4j;

/**
 * OllamaClient使用示例
 * 演示如何使用OllamaClient调用Ollama服务
 */
public class OllamaClientExample {
    
    public static void main(String[] args) {
        // 创建OllamaClient实例
        OllamaClient client = new OllamaClient();
        
        // 检查服务是否可用
        if (!client.isServiceAvailable()) {
            System.out.println("Ollama服务不可用，请确保Ollama正在运行");
            return;
        }
        
        System.out.println("=== Ollama客户端示例 ===");
        System.out.println("使用模型: " + client.getModel());
        System.out.println("服务地址: " + client.getBaseUrl());
        System.out.println();
        
        // 示例1: 简单聊天
        System.out.println("1. 简单聊天示例:");
        testSimpleChat(client);
        System.out.println();
        
        // 示例2: 请求代码生成
        System.out.println("2. 代码生成示例:");
        testCodeGeneration(client);
        System.out.println();
        
        // 示例3: 带系统提示的聊天
        System.out.println("3. 系统提示示例:");
        testSystemPrompt(client);
        System.out.println();
        
        // 示例4: 获取可用模型
        System.out.println("4. 获取可用模型:");
        testGetModels(client);
    }
    
    /**
     * 简单聊天示例
     */
    private static void testSimpleChat(OllamaClient client) {
        String prompt = "你好，请简单介绍一下你自己";
        OllamaClient.OllamaResponse response = client.chat(prompt);
        
        System.out.println("用户: " + prompt);
        System.out.println("助手: " + (response.isSuccess() ? response.getMainContent() : 
            "错误: " + response.getError()));
        System.out.println("响应JSON: " + response.toJson());
    }
    
    /**
     * 代码生成示例
     */
    private static void testCodeGeneration(OllamaClient client) {
        String prompt = "请写一个Java的快速排序算法实现";
        OllamaClient.OllamaResponse response = client.chat(prompt);
        
        System.out.println("用户: " + prompt);
        
        if (response.isSuccess()) {
            System.out.println("助手回复: " + response.getCleanText());
            
            if (response.isHasCodeBlock()) {
                System.out.println("检测到代码块:");
                System.out.println("  语言: " + (response.getCodeLanguage() != null ? 
                    response.getCodeLanguage() : "未指定"));
                System.out.println("  代码内容:");
                System.out.println("```");
                System.out.println(response.getCodeContent());
                System.out.println("```");
            } else {
                System.out.println("未检测到代码块");
            }
        } else {
            System.out.println("错误: " + response.getError());
            if (response.getErrorDetail() != null) {
                System.out.println("详细错误: " + response.getErrorDetail());
            }
        }
    }
    
    /**
     * 系统提示示例
     */
    private static void testSystemPrompt(OllamaClient client) {
        String systemPrompt = "你是一个专业的Java开发专家，请用简洁明了的方式回答问题";
        String userPrompt = "什么是Spring Boot的自动配置？";
        
        OllamaClient.OllamaResponse response = client.chat(userPrompt, systemPrompt);
        
        System.out.println("系统提示: " + systemPrompt);
        System.out.println("用户: " + userPrompt);
        System.out.println("助手: " + (response.isSuccess() ? response.getMainContent() : 
            "错误: " + response.getError()));
    }
    
    /**
     * 获取可用模型示例
     */
    private static void testGetModels(OllamaClient client) {
        String models = client.getAvailableModels();
        System.out.println("可用模型列表:");
        System.out.println(models);
    }
    
    /**
     * 演示不同的客户端配置
     */
    public static void demonstrateCustomConfiguration() {
        System.out.println("\n=== 自定义配置示例 ===");
        
        // 使用自定义URL和模型
        OllamaClient customClient = new OllamaClient("http://custom-ollama:11434", "llama3.1");
        customClient.setConnectTimeout(60);
        customClient.setReadTimeout(600);
        
        // 添加自定义请求头
        customClient.getCustomHeaders().put("X-Custom-Header", "custom-value");
        
        System.out.println("自定义客户端配置:");
        System.out.println("  URL: " + customClient.getBaseUrl());
        System.out.println("  模型: " + customClient.getModel());
        System.out.println("  连接超时: " + customClient.getConnectTimeout() + "秒");
        System.out.println("  读取超时: " + customClient.getReadTimeout() + "秒");
        System.out.println("  自定义请求头: " + customClient.getCustomHeaders());
    }
    
    /**
     * 演示错误处理
     */
    public static void demonstrateErrorHandling() {
        System.out.println("\n=== 错误处理示例 ===");
        
        // 使用不存在的服务地址
        OllamaClient errorClient = new OllamaClient("http://non-existent-server:11434", "non-existent-model");
        
        // 尝试调用
        OllamaClient.OllamaResponse response = errorClient.chat("测试消息");
        
        if (!response.isSuccess()) {
            System.out.println("预期的错误:");
            System.out.println("  错误信息: " + response.getError());
            System.out.println("  错误详情: " + response.getErrorDetail());
            System.out.println("  完整JSON: " + response.toJson());
        }
    }
}
