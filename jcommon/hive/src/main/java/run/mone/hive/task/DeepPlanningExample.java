package run.mone.hive.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Deep Planning功能的完整示例
 * 演示从斜杠命令解析到任务创建的完整流程
 */
public class DeepPlanningExample {
    
    private SlashCommandParser commandParser;
    private DeepPlanningProcessor planningProcessor;
    private FocusChainManager focusChainManager;
    private TaskCallbacks callbacks;
    private LLM llm;
    
    public static void main(String[] args) {
        DeepPlanningExample example = new DeepPlanningExample();
        example.runExample();
    }
    
    public void runExample() {
        System.out.println("=== Deep Planning Java版本示例 ===\n");
        
        // 初始化组件
        initializeComponents();
        
        // 演示完整的Deep Planning流程
        demonstrateDeepPlanningFlow();
    }
    
    private void initializeComponents() {
        // 创建模拟的LLM实现
        llm = new MockLLM();
        
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
     * 模拟的LLM实现
     */
    private static class MockLLM implements LLM {
        @Override
        public String sendMessage(String message) {
            // 模拟LLM响应
            if (message.contains("investigation") || message.contains("codebase")) {
                return "Based on my analysis, this is a Java project with modular architecture. " +
                       "I recommend implementing the authentication feature using standard Java security patterns.";
            } else if (message.contains("questions") || message.contains("clarify")) {
                return "I have identified the key technical decisions that need clarification for optimal implementation.";
            } else if (message.contains("implementation plan")) {
                return "I have created a comprehensive implementation plan with detailed specifications and step-by-step guidance.";
            } else {
                return "Mock LLM response for: " + message.substring(0, Math.min(50, message.length())) + "...";
            }
        }
        
        @Override
        public String sendMessage(String systemPrompt, String userMessage) {
            return "Mock LLM response with system prompt";
        }
    }
}
