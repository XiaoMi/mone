package run.mone.hive.task;

/**
 * remindClineInterval机制演示
 * 展示Focus Chain的提醒间隔是如何工作的
 */
public class RemindIntervalExample {
    
    public static void main(String[] args) {
        RemindIntervalExample example = new RemindIntervalExample();
        example.demonstrateRemindInterval();
    }
    
    public void demonstrateRemindInterval() {
        System.out.println("=== remindClineInterval 机制演示 ===\n");
        
        // 创建设置，提醒间隔设为3（方便演示）
        FocusChainSettings settings = new FocusChainSettings(true, 3);
        System.out.println("📋 Focus Chain设置: " + settings);
        System.out.println("🔄 提醒间隔: 每 " + settings.getRemindClineInterval() + " 次API请求提醒一次\n");
        
        // 创建任务状态和Focus Chain管理器
        TaskState taskState = new TaskState();
        FocusChainManager focusChainManager = createFocusChainManager(taskState, settings);
        
        // 模拟任务执行循环
        simulateTaskExecution(taskState, focusChainManager, settings);
    }
    
    /**
     * 模拟任务执行循环，演示remindClineInterval的工作机制
     */
    private void simulateTaskExecution(TaskState taskState, FocusChainManager focusChainManager, FocusChainSettings settings) {
        System.out.println("🚀 开始模拟任务执行循环...\n");
        
        // 模拟15次API请求
        for (int i = 1; i <= 15; i++) {
            System.out.println(String.format("📡 API请求 #%d:", i));
            
            // 1. 递增计数器（对应Cline中的第1589-1590行）
            taskState.incrementApiRequestCount();
            taskState.incrementApiRequestsSinceLastTodoUpdate();
            
            System.out.println(String.format("   总API请求次数: %d", taskState.getApiRequestCount()));
            System.out.println(String.format("   距离上次todo更新: %d 次请求", taskState.getApiRequestsSinceLastTodoUpdate()));
            
            // 2. 检查是否应该注入Focus Chain指令（对应Cline中的第467-468行）
            boolean shouldInclude = focusChainManager.shouldIncludeFocusChainInstructions();
            System.out.println(String.format("   应该注入Focus Chain指令: %s", shouldInclude));
            
            if (shouldInclude) {
                System.out.println("   🎯 触发原因分析:");
                analyzeWhyShouldInclude(taskState, settings, i);
                
                // 3. 如果需要注入指令，生成指令内容
                String instructions = focusChainManager.generateFocusChainInstructions();
                System.out.println("   📝 生成Focus Chain指令 (" + instructions.length() + " 字符)");
                
                // 4. 重置计数器（在loadContext中会调用）
                taskState.resetApiRequestsSinceLastTodoUpdate();
                taskState.setTodoListWasUpdatedByUser(false);
                System.out.println("   🔄 重置apiRequestsSinceLastTodoUpdate = 0");
            }
            
            // 5. 模拟LLM可能返回task_progress更新
            if (shouldInclude && Math.random() > 0.5) {
                String mockProgress = generateMockProgress(i);
                System.out.println("   🔧 LLM返回task_progress更新");
                focusChainManager.updateFCListFromToolResponse(mockProgress);
                System.out.println("   🔄 Focus Chain更新后，重置计数器");
            }
            
            System.out.println();
            
            // 暂停一下，方便观察
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("✅ 任务执行循环演示完成!\n");
        showSummary(settings);
    }
    
    /**
     * 分析为什么应该注入Focus Chain指令
     */
    private void analyzeWhyShouldInclude(TaskState taskState, FocusChainSettings settings, int currentRequest) {
        // 对应Cline中shouldIncludeFocusChainInstructions的逻辑
        boolean reachedReminderInterval = 
            taskState.getApiRequestsSinceLastTodoUpdate() >= settings.getRemindClineInterval();
        boolean isFirstApiRequest = 
            taskState.getApiRequestCount() == 1 && taskState.getCurrentFocusChainChecklist() == null;
        boolean hasNoTodoListAfterMultipleRequests = 
            taskState.getCurrentFocusChainChecklist() == null && taskState.getApiRequestCount() >= 2;
        
        if (reachedReminderInterval) {
            System.out.println(String.format("     ⏰ 达到提醒间隔: %d >= %d", 
                taskState.getApiRequestsSinceLastTodoUpdate(), settings.getRemindClineInterval()));
        }
        if (isFirstApiRequest) {
            System.out.println("     🆕 首次API请求且无现有列表");
        }
        if (hasNoTodoListAfterMultipleRequests) {
            System.out.println("     ❌ 多次请求后仍无todo列表");
        }
    }
    
    /**
     * 生成模拟的进度更新
     */
    private String generateMockProgress(int requestNumber) {
        String[] tasks = {
            "分析需求", "设计架构", "实现核心功能", 
            "编写测试", "优化性能", "部署上线"
        };
        
        StringBuilder progress = new StringBuilder();
        for (int i = 0; i < tasks.length; i++) {
            if (i < (requestNumber - 1) / 3) {
                progress.append("- [x] ").append(tasks[i]).append("\n");
            } else {
                progress.append("- [ ] ").append(tasks[i]).append("\n");
            }
        }
        return progress.toString().trim();
    }
    
    /**
     * 创建Focus Chain管理器
     */
    private FocusChainManager createFocusChainManager(TaskState taskState, FocusChainSettings settings) {
        LLMTaskProcessor mockLlm = new LLMTaskProcessor() {
            @Override
            public String sendMessage(String message) {
                return "Mock LLM response";
            }
            
            @Override
            public String sendMessage(String systemPrompt, String userMessage) {
                return "Mock LLM response";
            }
        };
        
        FocusChainManager manager = new FocusChainManager(
            "remind-demo", taskState, Mode.ACT, 
            "./remind-demo", settings, mockLlm
        );
        
        manager.setSayCallback(message -> 
            System.out.println("   [FOCUS_CHAIN] " + message));
        manager.setPostStateToWebviewCallback(() -> {});
        
        return manager;
    }
    
    /**
     * 显示总结信息
     */
    private void showSummary(FocusChainSettings settings) {
        System.out.println("📊 remindClineInterval 机制总结:\n");
        
        System.out.println("🔄 工作流程:");
        System.out.println("1. 每次API请求 → apiRequestsSinceLastTodoUpdate++");
        System.out.println("2. 检查条件 → apiRequestsSinceLastTodoUpdate >= remindClineInterval");
        System.out.println("3. 达到间隔 → 注入Focus Chain指令到LLM提示词");
        System.out.println("4. 重置计数 → apiRequestsSinceLastTodoUpdate = 0");
        System.out.println("5. LLM响应 → 可能包含task_progress更新");
        System.out.println("6. 更新进度 → 再次重置计数器\n");
        
        System.out.println("⚙️ 配置说明:");
        System.out.println("• remindClineInterval = " + settings.getRemindClineInterval() + 
                          " → 每 " + settings.getRemindClineInterval() + " 次API请求提醒一次");
        System.out.println("• 较小的值 → 更频繁的提醒，更好的任务跟踪");
        System.out.println("• 较大的值 → 较少的提醒，减少提示词开销");
        System.out.println("• 推荐值: 3-10，默认值: 6\n");
        
        System.out.println("🎯 触发条件（任一满足即触发）:");
        System.out.println("1. ⏰ 达到提醒间隔 (apiRequestsSinceLastTodoUpdate >= remindClineInterval)");
        System.out.println("2. 📋 Plan模式");
        System.out.println("3. 🔄 从Plan模式切换到Act模式");
        System.out.println("4. ✏️ 用户手动编辑了todo列表");
        System.out.println("5. 🆕 首次API请求且无现有列表");
        System.out.println("6. ❌ 多次请求后仍无todo列表");
        
        System.out.println("\n💡 这个机制确保AI不会忘记长期任务目标，保持任务执行的连续性和一致性。");
    }
}
