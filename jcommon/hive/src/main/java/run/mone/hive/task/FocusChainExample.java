package run.mone.hive.task;

import java.io.IOException;
import java.util.Scanner;

/**
 * Focus Chain功能的示例使用类
 * 演示如何集成和使用Focus Chain功能
 */
public class FocusChainExample {
    
    private FocusChainManager focusChainManager;
    private TaskState taskState;
    private LLMTaskProcessor llm;
    private TaskCallbacks callbacks;
    
    public static void main(String[] args) {
        FocusChainExample example = new FocusChainExample();
        example.runExample();
    }
    
    public void runExample() {
        System.out.println("=== Focus Chain Java版本示例 ===\n");
        
        // 初始化组件
        initializeComponents();
        
        // 模拟任务执行流程
        simulateTaskFlow();
    }
    
    private void initializeComponents() {
        // 创建任务状态
        taskState = new TaskState();
        
        // 创建Focus Chain设置
        FocusChainSettings settings = new FocusChainSettings(true, 6);
        
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
            public void onProgressUpdated(String taskId, String progress) {
                System.out.println(String.format("[PROGRESS] Task %s: %s", taskId, progress));
            }
        };
        
        // 创建Focus Chain管理器
        String taskId = "example-task-001";
        String taskDirectory = "./focus-chain-demo";
        
        focusChainManager = new FocusChainManager(
            taskId, taskState, Mode.PLAN, taskDirectory, settings, llm
        );
        
        // 设置回调
        focusChainManager.setSayCallback(message -> callbacks.say("FOCUS_CHAIN", message));
        focusChainManager.setPostStateToWebviewCallback(callbacks::postStateToWebview);
        
        try {
            // 启动文件监控
            focusChainManager.setupFocusChainFileWatcher();
        } catch (IOException e) {
            System.err.println("Failed to setup file watcher: " + e.getMessage());
        }
        
        System.out.println("✅ Focus Chain组件初始化完成\n");
    }
    
    private void simulateTaskFlow() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("🎯 开始模拟任务流程...\n");
        
        // 1. 模拟Plan模式
        System.out.println("1️⃣ 当前处于PLAN模式");
        taskState.incrementApiRequestCount();
        
        if (focusChainManager.shouldIncludeFocusChainInstructions()) {
            String instructions = focusChainManager.generateFocusChainInstructions();
            System.out.println("📋 生成的Focus Chain指令:");
            System.out.println(instructions);
        }
        
        // 模拟AI响应创建初始待办列表
        String initialTodoList = 
            "- [ ] 分析用户需求\n" +
            "- [ ] 设计系统架构\n" +
            "- [ ] 创建项目结构\n" +
            "- [ ] 实现核心功能";
        
        focusChainManager.updateFCListFromToolResponse(initialTodoList);
        
        System.out.println("\n按Enter键继续到下一步...");
        scanner.nextLine();
        
        // 2. 切换到Act模式
        System.out.println("\n2️⃣ 切换到ACT模式");
        focusChainManager.updateMode(Mode.ACT);
        taskState.setDidRespondToPlanAskBySwitchingMode(true);
        taskState.incrementApiRequestCount();
        
        if (focusChainManager.shouldIncludeFocusChainInstructions()) {
            String instructions = focusChainManager.generateFocusChainInstructions();
            System.out.println("📋 切换模式后的指令:");
            System.out.println(instructions);
        }
        
        System.out.println("\n按Enter键继续...");
        scanner.nextLine();
        
        // 3. 模拟进度更新
        System.out.println("\n3️⃣ 模拟任务进度更新");
        String updatedTodoList = 
            "- [x] 分析用户需求\n" +
            "- [x] 设计系统架构\n" +
            "- [ ] 创建项目结构\n" +
            "- [ ] 实现核心功能\n" +
            "- [ ] 编写测试用例\n" +
            "- [ ] 部署到测试环境";
        
        focusChainManager.updateFCListFromToolResponse(updatedTodoList);
        
        System.out.println("\n按Enter键继续...");
        scanner.nextLine();
        
        // 4. 模拟多次API请求后的提醒
        System.out.println("\n4️⃣ 模拟多次API请求后的自动提醒");
        for (int i = 0; i < 7; i++) {
            taskState.incrementApiRequestCount();
        }
        
        if (focusChainManager.shouldIncludeFocusChainInstructions()) {
            String instructions = focusChainManager.generateFocusChainInstructions();
            System.out.println("⏰ 到达提醒间隔，生成的指令:");
            System.out.println(instructions);
        }
        
        System.out.println("\n按Enter键完成示例...");
        scanner.nextLine();
        
        // 5. 任务完成检查
        System.out.println("\n5️⃣ 任务完成检查");
        focusChainManager.checkIncompleteProgressOnCompletion();
        
        // 清理资源
        focusChainManager.dispose();
        
        System.out.println("\n✅ Focus Chain示例演示完成!");
        System.out.println("📁 请查看 ./focus-chain-demo/focus-chain.md 文件查看生成的待办列表");
        
        scanner.close();
    }
    
    /**
     * 模拟的LLM实现
     */
    private static class MockLLM implements LLMTaskProcessor {
        @Override
        public String sendMessage(String message) {
            return "Mock LLM Response: " + message.substring(0, Math.min(50, message.length())) + "...";
        }
        
        @Override
        public String sendMessage(String systemPrompt, String userMessage) {
            return "Mock LLM Response with system prompt";
        }
    }
}
