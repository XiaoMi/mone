package run.mone.hive.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLMProvider;

/**
 * Deep Planning功能的完整示例
 * 演示从斜杠命令解析到任务创建的完整流程
 */
public class DeepPlanningExample {
    
    private SlashCommandParser commandParser;
    private DeepPlanningProcessor planningProcessor;
    private FocusChainManager focusChainManager;
    private TaskCallbacks callbacks;
    private LLMTaskProcessor llm;
    
    public static void main(String[] args) {
        DeepPlanningExample example = new DeepPlanningExample();
        example.runExample();
    }
    
    public void runExample() {
        System.out.println("=== Deep Planning Java版本示例 ===\n");
        
        // 显示LLM配置说明
        displayLLMConfigurationInfo();
        
        // 初始化组件
        initializeComponents();
        
        // 演示完整的Deep Planning流程
        demonstrateDeepPlanningFlow();
    }
    
    /**
     * 显示LLM配置说明
     */
    private void displayLLMConfigurationInfo() {
        System.out.println("🔧 LLM配置说明:");
        System.out.println("本示例将尝试使用真实的LLM服务 (DeepSeek)");
        System.out.println("如需配置API密钥，请设置以下环境变量或配置文件:");
        System.out.println("- DEEPSEEK_API_KEY: DeepSeek API密钥");
        System.out.println("- DEEPSEEK_API_URL: DeepSeek API地址 (可选)");
        System.out.println("如果真实LLM不可用，将自动回退到模拟实现\n");
    }
    
    private void initializeComponents() {
        // 创建实际的LLM实现
        llm = createRealLLM();
        
        // 创建回调实现
        callbacks = new TaskCallbacks() {
            @Override
            public void say(String type, String message) {
                System.out.println(String.format("[%s] %s", type.toUpperCase(), message));
            }
            
            @Override
            public void postStateToWebview() {
                System.out.println("[WEBVIEW] State updated");
            }
            
            @Override
            public void onTaskCompleted(String taskId, boolean success) {
                System.out.println(String.format("[TASK] Task %s %s", taskId, success ? "completed successfully" : "failed"));
            }
            
            @Override
            public void onProgressUpdated(String taskId, String progress) {
                System.out.println(String.format("[PROGRESS] Task %s: %s", taskId, progress));
            }
        };
        
        // 创建Focus Chain管理器
        TaskState taskState = new TaskState();
        FocusChainSettings focusChainSettings = new FocusChainSettings(true, 6);
        
        focusChainManager = new FocusChainManager(
            "deep-planning-demo", taskState, Mode.PLAN, 
            "./deep-planning-demo", focusChainSettings, llm
        );
        focusChainManager.setSayCallback(message -> callbacks.say("FOCUS_CHAIN", message));
        focusChainManager.setPostStateToWebviewCallback(callbacks::postStateToWebview);
        
        // 创建Deep Planning处理器
        planningProcessor = new DeepPlanningProcessor(llm, callbacks, focusChainManager);
        
        // 创建斜杠命令解析器
        commandParser = new SlashCommandParser();
        
        // 显示LLM配置信息
        if (llm instanceof LLMTaskProcessorImpl) {
            System.out.println("✅ 使用真实LLM服务 (DeepSeek)");
        } else {
            System.out.println("⚠️ 使用回退模拟LLM (真实LLM服务不可用)");
        }
        
        System.out.println("✅ Deep Planning组件初始化完成\n");
    }
    
    private void demonstrateDeepPlanningFlow() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("🎯 开始演示Deep Planning流程...\n");
        
        // 1. 演示斜杠命令解析
        System.out.println("1️⃣ 斜杠命令解析演示");
        String userInput = "<task>/deep-planning 添加用户认证功能，支持JWT令牌和基于角色的访问控制</task>";
        System.out.println("用户输入: " + userInput);
        
        FocusChainSettings settings = new FocusChainSettings(true, 6);
        SlashCommandParser.ParseResult parseResult = commandParser.parseSlashCommands(userInput, settings);
        
        System.out.println("\n📋 解析结果:");
        System.out.println("是否需要检查规则文件: " + parseResult.needsClinerulesFileCheck());
        System.out.println("\n生成的提示词预览 (前200字符):");
        String prompt = parseResult.getProcessedText();
        System.out.println(prompt.substring(0, Math.min(200, prompt.length())) + "...\n");
        
        System.out.println("按Enter键继续到下一步...");
        scanner.nextLine();
        
        // 2. 演示Deep Planning处理器
        System.out.println("\n2️⃣ Deep Planning处理器演示");
        System.out.println("模拟执行四步骤Deep Planning流程...\n");
        
        try {
            // 创建工作目录
            String workingDir = "./deep-planning-demo";
            Files.createDirectories(Paths.get(workingDir));
            
            // 执行Deep Planning
            String taskId = planningProcessor.executeDeepPlanning(
                "添加用户认证功能，支持JWT令牌和基于角色的访问控制", 
                workingDir
            );
            
            System.out.println("\n✅ Deep Planning流程完成!");
            System.out.println("📋 任务ID: " + taskId);
            
        } catch (IOException e) {
            System.err.println("创建工作目录失败: " + e.getMessage());
        }
        
        System.out.println("\n按Enter键继续查看生成的文件...");
        scanner.nextLine();
        
        // 3. 显示生成的文件
        System.out.println("\n3️⃣ 查看生成的文件");
        displayGeneratedFiles();
        
        System.out.println("\n按Enter键查看Focus Chain集成...");
        scanner.nextLine();
        
        // 4. 演示Focus Chain集成
        System.out.println("\n4️⃣ Focus Chain集成演示");
        demonstrateFocusChainIntegration();
        
        System.out.println("\n按Enter键完成演示...");
        scanner.nextLine();
        
        System.out.println("\n✅ Deep Planning Java版本演示完成!");
        System.out.println("📁 请查看 ./deep-planning-demo/ 目录中生成的文件");
        
        scanner.close();
    }
    
    private void displayGeneratedFiles() {
        try {
            // 显示实施计划文件
            String planPath = "./deep-planning-demo/implementation_plan.md";
            if (Files.exists(Paths.get(planPath))) {
                System.out.println("📄 Implementation Plan (implementation_plan.md):");
                String content = Files.readString(Paths.get(planPath));
                String[] lines = content.split("\n");
                
                // 显示前20行
                for (int i = 0; i < Math.min(20, lines.length); i++) {
                    System.out.println("  " + lines[i]);
                }
                if (lines.length > 20) {
                    System.out.println("  ... (省略 " + (lines.length - 20) + " 行)");
                }
            }
            
            // 显示Focus Chain文件
            String focusChainPath = "./deep-planning-demo/focus-chain.md";
            if (Files.exists(Paths.get(focusChainPath))) {
                System.out.println("\n📋 Focus Chain List (focus-chain.md):");
                String content = Files.readString(Paths.get(focusChainPath));
                System.out.println(content);
            }
            
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
        }
    }
    
    private void demonstrateFocusChainIntegration() {
        System.out.println("🔗 Focus Chain与Deep Planning的集成:");
        System.out.println("✓ Deep Planning生成的任务步骤自动转换为Focus Chain待办列表");
        System.out.println("✓ 支持实时进度跟踪和用户手动编辑");
        System.out.println("✓ 任务步骤与实施计划文档保持同步");
        System.out.println("✓ 支持长期任务的上下文保持");
        
        // 模拟一些进度更新
        System.out.println("\n📊 模拟进度更新:");
        String updatedProgress = 
            "- [x] Create base interfaces and data structures\n" +
            "- [x] Implement core functionality classes\n" + 
            "- [ ] Add configuration management\n" +
            "- [ ] Integrate with existing system\n" +
            "- [ ] Add comprehensive testing\n" +
            "- [ ] Update documentation";
        
        focusChainManager.updateFCListFromToolResponse(updatedProgress);
        
        System.out.println("\n🎯 当前任务状态:");
        FocusChainFileUtils.FocusChainCounts counts = 
            FocusChainFileUtils.parseFocusChainListCounts(updatedProgress);
        System.out.println(String.format("总任务数: %d", counts.getTotalItems()));
        System.out.println(String.format("已完成: %d", counts.getCompletedItems()));
        System.out.println(String.format("未完成: %d", counts.getIncompleteItems()));
        System.out.println(String.format("完成率: %.1f%%", 
            (double) counts.getCompletedItems() / counts.getTotalItems() * 100));
    }
    
    /**
     * 创建真实的LLM实现
     */
    private LLMTaskProcessor createRealLLM() {
        try {
            // 从环境变量或系统属性获取API配置
            String apiKey = System.getenv("DEEPSEEK_API_KEY");
            String apiUrl = System.getenv("DEEPSEEK_API_URL");
            
            if (apiKey == null) {
                apiKey = System.getProperty("deepseek.api.key");
            }
            if (apiUrl == null) {
                apiUrl = System.getProperty("deepseek.api.url", "https://api.deepseek.com");
            }
            
            // 创建LLM配置
            LLMConfig.LLMConfigBuilder configBuilder = LLMConfig.builder()
                    .model("deepseek-chat")  // 使用deepseek模型
                    .llmProvider(LLMProvider.DEEPSEEK)
                    .temperature(0.1)
                    .maxTokens(4000)
                    .debug(true);
            
            // 如果有API密钥，则设置
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                configBuilder.token(apiKey);
            }
            
            // 如果有自定义API地址，则设置
            if (apiUrl != null && !apiUrl.trim().isEmpty()) {
                configBuilder.url(apiUrl);
            }
            
            LLMConfig config = configBuilder.build();
            
            // 创建真实的LLM任务处理器
            LLMTaskProcessor processor = new LLMTaskProcessorImpl(config);
            
            // 测试LLM连接
            String testResponse = processor.sendMessage("Hello, this is a connection test.");
            if (testResponse != null && !testResponse.trim().isEmpty()) {
                System.out.println("✅ LLM连接测试成功");
                return processor;
            } else {
                throw new RuntimeException("LLM returned empty response");
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ 创建真实LLM失败，回退到模拟实现: " + e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("API key")) {
                System.err.println("💡 提示: 请设置DEEPSEEK_API_KEY环境变量");
            }
            return createFallbackLLM();
        }
    }
    
    /**
     * 创建回退的模拟LLM实现（当真实LLM创建失败时使用）
     */
    private LLMTaskProcessor createFallbackLLM() {
        return new LLMTaskProcessor() {
            @Override
            public String sendMessage(String message) {
                System.out.println("[FALLBACK_LLM] Processing message: " + message.substring(0, Math.min(100, message.length())) + "...");
                
                // 根据消息内容生成更智能的响应
                if (message.contains("investigation") || message.contains("codebase") || message.contains("STEP 1")) {
                    return generateInvestigationResponse();
                } else if (message.contains("questions") || message.contains("clarify") || message.contains("STEP 2")) {
                    return generateQuestionsResponse();
                } else if (message.contains("implementation plan") || message.contains("STEP 3")) {
                    return generateImplementationPlanResponse();
                } else if (message.contains("task") || message.contains("STEP 4")) {
                    return generateTaskCreationResponse();
                } else {
                    return "Based on the analysis of your request, I understand you want to implement user authentication with JWT tokens and role-based access control. This is a comprehensive feature that requires careful planning and implementation.";
                }
            }
            
            @Override
            public String sendMessage(String systemPrompt, String userMessage) {
                System.out.println("[FALLBACK_LLM] Processing with system prompt");
                return sendMessage(userMessage);
            }
            
            private String generateInvestigationResponse() {
                return """
                Based on my analysis of the codebase, I've identified the following:
                
                **Project Structure:**
                - This is a Java project with Maven build system
                - Uses Spring Boot framework for web development
                - Already has modular architecture with clear separation of concerns
                - Existing authentication infrastructure that can be extended
                
                **Key Findings:**
                - Current security configuration is basic
                - Database schema supports user management
                - REST API endpoints are well-structured
                - Logging and monitoring capabilities are in place
                
                **Recommendations:**
                - Implement JWT-based authentication using Spring Security
                - Add role-based access control (RBAC) with custom annotations
                - Integrate with existing user management system
                - Ensure backward compatibility with current authentication
                """;
            }
            
            private String generateQuestionsResponse() {
                return """
                I need clarification on a few key points:
                
                1. **Token Expiration**: What should be the JWT token expiration time? (e.g., 1 hour, 24 hours)
                2. **Roles**: What specific roles do you need? (e.g., ADMIN, USER, MODERATOR)
                3. **Refresh Tokens**: Do you want to implement refresh token functionality?
                4. **Database**: Should we store JWT tokens in database or keep them stateless?
                5. **Migration**: How should we handle existing user sessions during the upgrade?
                """;
            }
            
            private String generateImplementationPlanResponse() {
                return """
                # Implementation Plan
                
                ## Overview
                Implement JWT-based authentication with role-based access control for the Java application.
                
                ## Files to Create/Modify
                - JwtAuthenticationFilter.java
                - JwtTokenProvider.java  
                - UserRole.java (enum)
                - SecurityConfig.java (update)
                - AuthController.java (update)
                
                ## Implementation Steps
                1. Add JWT dependencies to pom.xml
                2. Create JWT token provider and filter
                3. Implement role-based access control
                4. Update security configuration
                5. Add authentication endpoints
                6. Create unit and integration tests
                
                ## Testing Strategy
                - Unit tests for JWT token generation/validation
                - Integration tests for authentication flows
                - Security tests for role-based access
                """;
            }
            
            private String generateTaskCreationResponse() {
                return """
                Task created successfully for implementing JWT authentication with RBAC.
                
                **Task Progress:**
                - [ ] Set up JWT dependencies and configuration
                - [ ] Implement JWT token provider
                - [ ] Create authentication filter
                - [ ] Add role-based access control
                - [ ] Update security configuration
                - [ ] Implement authentication endpoints
                - [ ] Add comprehensive testing
                - [ ] Update documentation
                
                The implementation plan has been saved and the task is ready for execution.
                """;
            }
        };
    }
}
