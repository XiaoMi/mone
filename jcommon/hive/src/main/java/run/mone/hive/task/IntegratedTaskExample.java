package run.mone.hive.task;

import java.util.Scanner;

/**
 * 集成任务执行示例
 * 演示Focus Chain如何集成到主要的任务执行循环中
 */
public class IntegratedTaskExample {
    
    public static void main(String[] args) {
        IntegratedTaskExample example = new IntegratedTaskExample();
        example.runIntegratedExample();
    }
    
    public void runIntegratedExample() {
        System.out.println("=== 集成任务执行循环演示 ===\n");
        
        // 1. 初始化组件
        TaskState taskState = new TaskState();
        FocusChainSettings focusChainSettings = new FocusChainSettings(true, 3);
        
        // 创建回调
        TaskCallbacks callbacks = createCallbacks();
        
        // 创建LLM模拟
        LLM llm = createMockLLM();
        
        // 创建Focus Chain管理器
        FocusChainManager focusChainManager = new FocusChainManager(
            "integrated-demo", taskState, Mode.ACT, 
            "./integrated-demo", focusChainSettings, llm
        );
        focusChainManager.setSayCallback(message -> callbacks.say("FOCUS_CHAIN", message));
        focusChainManager.setPostStateToWebviewCallback(callbacks::postStateToWebview);
        
        // 创建命令解析器
        SlashCommandParser commandParser = new SlashCommandParser();
        
        // 创建任务执行循环
        TaskExecutionLoop taskLoop = new TaskExecutionLoop(
            taskState, focusChainManager, commandParser, 
            llm, callbacks, focusChainSettings
        );
        
        System.out.println("✅ 组件初始化完成\n");
        
        // 2. 演示任务执行流程
        demonstrateTaskExecution(taskLoop, focusChainManager);
    }
    
    private void demonstrateTaskExecution(TaskExecutionLoop taskLoop, FocusChainManager focusChainManager) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("🚀 开始任务执行循环演示...\n");
        
        // 模拟用户输入
        String userTask = "创建一个用户管理系统，包括用户注册、登录和权限管理功能";
        System.out.println("用户任务: " + userTask);
        System.out.println("\n按Enter键开始执行...");
        scanner.nextLine();
        
        // 演示Focus Chain集成的关键点
        demonstrateFocusChainIntegration(taskLoop, focusChainManager, userTask);
        
        System.out.println("\n按Enter键查看Focus Chain状态...");
        scanner.nextLine();
        
        // 显示Focus Chain状态
        showFocusChainStatus(focusChainManager);
        
        System.out.println("\n按Enter键模拟工具调用和进度更新...");
        scanner.nextLine();
        
        // 模拟工具调用和进度更新
        simulateToolCallsAndProgress(focusChainManager);
        
        System.out.println("\n✅ 集成演示完成!");
        scanner.close();
    }
    
    /**
     * 演示Focus Chain集成的关键点
     */
    private void demonstrateFocusChainIntegration(TaskExecutionLoop taskLoop, 
                                                 FocusChainManager focusChainManager, 
                                                 String userTask) {
        
        System.out.println("🔗 Focus Chain集成关键点演示:\n");
        
        // 1. 演示指令注入条件检查
        System.out.println("1️⃣ 检查Focus Chain指令注入条件:");
        boolean shouldInclude = focusChainManager.shouldIncludeFocusChainInstructions();
        System.out.println("   应该注入指令: " + shouldInclude);
        if (shouldInclude) {
            System.out.println("   原因: 首次API请求且无现有列表");
        }
        
        // 2. 演示指令生成
        System.out.println("\n2️⃣ 生成Focus Chain指令:");
        String instructions = focusChainManager.generateFocusChainInstructions();
        System.out.println("   指令长度: " + instructions.length() + " 字符");
        System.out.println("   指令预览: " + instructions.substring(0, Math.min(100, instructions.length())) + "...");
        
        // 3. 演示上下文加载（模拟loadContext方法）
        System.out.println("\n3️⃣ 模拟上下文加载过程:");
        System.out.println("   原始用户内容: " + userTask);
        System.out.println("   + 环境详情");
        System.out.println("   + Focus Chain指令 ✅");
        System.out.println("   = 完整的LLM输入内容");
        
        // 4. 模拟任务执行开始
        System.out.println("\n4️⃣ 启动任务执行循环:");
        System.out.println("   [TASK_LOOP] 开始处理任务...");
        System.out.println("   [TASK_LOOP] API请求计数: 1");
        System.out.println("   [TASK_LOOP] Focus Chain指令已注入");
        
        // 模拟LLM响应创建初始todo列表
        String initialTodoList = 
            "- [ ] 设计用户数据模型\n" +
            "- [ ] 创建用户注册功能\n" +
            "- [ ] 实现用户登录验证\n" +
            "- [ ] 添加权限管理系统\n" +
            "- [ ] 编写单元测试\n" +
            "- [ ] 部署和文档";
        
        focusChainManager.updateFCListFromToolResponse(initialTodoList);
        System.out.println("   [LLM_RESPONSE] 创建了初始todo列表");
    }
    
    /**
     * 显示Focus Chain状态
     */
    private void showFocusChainStatus(FocusChainManager focusChainManager) {
        System.out.println("📋 当前Focus Chain状态:\n");
        
        // 读取当前列表
        String currentList = focusChainManager.readFocusChainFromDisk();
        if (currentList != null) {
            System.out.println("当前待办列表:");
            System.out.println(currentList);
            
            // 解析统计信息
            FocusChainFileUtils.FocusChainCounts counts = 
                FocusChainFileUtils.parseFocusChainListCounts(currentList);
            System.out.println("\n📊 统计信息:");
            System.out.println("总任务数: " + counts.getTotalItems());
            System.out.println("已完成: " + counts.getCompletedItems());
            System.out.println("未完成: " + counts.getIncompleteItems());
            System.out.println("完成率: " + String.format("%.1f%%", 
                (double) counts.getCompletedItems() / counts.getTotalItems() * 100));
        } else {
            System.out.println("暂无Focus Chain列表");
        }
    }
    
    /**
     * 模拟工具调用和进度更新
     */
    private void simulateToolCallsAndProgress(FocusChainManager focusChainManager) {
        System.out.println("🔧 模拟工具调用和进度更新:\n");
        
        // 模拟几个工具调用周期
        String[] progressUpdates = {
            "- [x] 设计用户数据模型\n- [x] 创建用户注册功能\n- [ ] 实现用户登录验证\n- [ ] 添加权限管理系统\n- [ ] 编写单元测试\n- [ ] 部署和文档",
            "- [x] 设计用户数据模型\n- [x] 创建用户注册功能\n- [x] 实现用户登录验证\n- [x] 添加权限管理系统\n- [ ] 编写单元测试\n- [ ] 部署和文档",
            "- [x] 设计用户数据模型\n- [x] 创建用户注册功能\n- [x] 实现用户登录验证\n- [x] 添加权限管理系统\n- [x] 编写单元测试\n- [x] 部署和文档"
        };
        
        for (int i = 0; i < progressUpdates.length; i++) {
            System.out.println(String.format("🔄 工具调用 #%d:", i + 1));
            System.out.println("   [TOOL_EXECUTOR] 执行工具调用...");
            System.out.println("   [TOOL_EXECUTOR] 检测到task_progress参数");
            
            // 更新进度
            focusChainManager.updateFCListFromToolResponse(progressUpdates[i]);
            
            // 显示更新结果
            FocusChainFileUtils.FocusChainCounts counts = 
                FocusChainFileUtils.parseFocusChainListCounts(progressUpdates[i]);
            System.out.println(String.format("   [FOCUS_CHAIN] 进度更新: %d/%d 完成 (%.1f%%)", 
                counts.getCompletedItems(), counts.getTotalItems(),
                (double) counts.getCompletedItems() / counts.getTotalItems() * 100));
            
            try {
                Thread.sleep(1000); // 暂停1秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\n🎉 所有任务已完成!");
        
        // 模拟任务完成处理
        System.out.println("\n🏁 任务完成处理:");
        System.out.println("   [ATTEMPT_COMPLETION] 检测任务完成");
        System.out.println("   [FOCUS_CHAIN] 分析未完成项目...");
        focusChainManager.analyzeIncompleteItemsOnCompletion();
        System.out.println("   [FOCUS_CHAIN] 任务完成分析完毕");
    }
    
    /**
     * 创建回调实现
     */
    private TaskCallbacks createCallbacks() {
        return new TaskCallbacks() {
            @Override
            public void say(String type, String message) {
                System.out.println(String.format("[%s] %s", type, message));
            }
            
            @Override
            public void postStateToWebview() {
                System.out.println("[WEBVIEW] State updated");
            }
            
            @Override
            public void onTaskCompleted(String taskId, boolean success) {
                System.out.println(String.format("[TASK] Task %s %s", 
                    taskId, success ? "completed successfully" : "failed"));
            }
            
            @Override
            public void onProgressUpdated(String taskId, String progress) {
                System.out.println(String.format("[PROGRESS] Task %s progress updated", taskId));
            }
        };
    }
    
    /**
     * 创建模拟LLM
     */
    private LLM createMockLLM() {
        return new LLM() {
            @Override
            public String sendMessage(String message) {
                // 模拟LLM响应，包含工具调用
                if (message.contains("Focus Chain Instructions")) {
                    return "I'll create a todo list for this task:\n\n" +
                           "tool_use: task_progress\n" +
                           "- [ ] 设计用户数据模型\n" +
                           "- [ ] 创建用户注册功能\n" +
                           "- [ ] 实现用户登录验证\n" +
                           "- [ ] 添加权限管理系统\n" +
                           "- [ ] 编写单元测试\n" +
                           "- [ ] 部署和文档";
                } else {
                    return "Mock LLM response for: " + message.substring(0, Math.min(50, message.length()));
                }
            }
            
            @Override
            public String sendMessage(String systemPrompt, String userMessage) {
                return sendMessage(userMessage);
            }
        };
    }
}
