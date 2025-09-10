package run.mone.hive.task;

import java.util.List;
import java.util.ArrayList;

/**
 * 任务执行循环 - 对应Cline的主要执行逻辑
 * 集成Focus Chain和Deep Planning功能
 */
public class TaskExecutionLoop {
    
    private final TaskState taskState;
    private final FocusChainManager focusChainManager;
    private final SlashCommandParser commandParser;
    private final LLM llm;
    private final TaskCallbacks callbacks;
    private final FocusChainSettings focusChainSettings;
    
    private static final int MAX_REQUESTS_PER_TASK = 100;
    
    public TaskExecutionLoop(
        TaskState taskState,
        FocusChainManager focusChainManager,
        SlashCommandParser commandParser,
        LLM llm,
        TaskCallbacks callbacks,
        FocusChainSettings focusChainSettings
    ) {
        this.taskState = taskState;
        this.focusChainManager = focusChainManager;
        this.commandParser = commandParser;
        this.llm = llm;
        this.callbacks = callbacks;
        this.focusChainSettings = focusChainSettings;
    }
    
    /**
     * 启动任务循环 - 对应initiateTaskLoop
     */
    public void initiateTaskLoop(String userContent) {
        String nextUserContent = userContent;
        boolean includeFileDetails = true;
        
        while (!taskState.isAbort()) {
            boolean didEndLoop = recursivelyMakeRequests(nextUserContent, includeFileDetails);
            includeFileDetails = false; // 只在第一次需要文件详情
            
            if (didEndLoop) {
                break;
            } else {
                // 如果没有使用工具，强制继续任务
                nextUserContent = "Please continue with the task or call attempt_completion if you're finished.";
                taskState.incrementConsecutiveMistakeCount();
            }
            
            // 检查最大请求限制
            if (taskState.getApiRequestCount() >= MAX_REQUESTS_PER_TASK) {
                callbacks.say("WARNING", "Reached maximum requests per task limit");
                break;
            }
        }
    }
    
    /**
     * 递归处理请求 - 对应recursivelyMakeClineRequests
     */
    public boolean recursivelyMakeRequests(String userContent, boolean includeFileDetails) {
        try {
            // 1. 增加API请求计数
            taskState.incrementApiRequestCount();
            taskState.incrementApiRequestsSinceLastTodoUpdate();
            
            // 2. 加载上下文并注入Focus Chain指令
            String processedContent = loadContext(userContent, includeFileDetails);
            
            // 3. 解析斜杠命令
            SlashCommandParser.ParseResult parseResult = commandParser.parseSlashCommands(
                processedContent, focusChainSettings
            );
            
            // 4. 发送到LLM
            String llmResponse = llm.sendMessage(parseResult.getProcessedText());
            
            // 5. 处理LLM响应
            boolean didUseTools = processLLMResponse(llmResponse);
            
            if (!didUseTools) {
                // 如果没有使用工具，需要提示继续
                return false;
            }
            
            // 6. 检查是否需要递归调用
            if (shouldContinueRecursion(llmResponse)) {
                return recursivelyMakeRequests(extractNextUserContent(llmResponse), false);
            }
            
            return true;
            
        } catch (Exception e) {
            callbacks.say("ERROR", "Error in recursive request processing: " + e.getMessage());
            return true; // 结束循环
        }
    }
    
    /**
     * 加载上下文 - 对应loadContext方法
     * 这是Focus Chain指令注入的关键位置
     */
    private String loadContext(String userContent, boolean includeFileDetails) {
        List<String> contentParts = new ArrayList<>();
        contentParts.add(userContent);
        
        // 添加环境详情（如果需要）
        if (includeFileDetails) {
            contentParts.add(getEnvironmentDetails());
        }
        
        // ⭐ 关键：注入Focus Chain指令
        // 对应Cline中的第2249-2259行逻辑
        if (focusChainManager != null && focusChainManager.shouldIncludeFocusChainInstructions()) {
            String focusChainInstructions = focusChainManager.generateFocusChainInstructions();
            contentParts.add(focusChainInstructions);
            
            // 重置计数器和标志
            taskState.resetApiRequestsSinceLastTodoUpdate();
            taskState.setTodoListWasUpdatedByUser(false);
            
            callbacks.say("FOCUS_CHAIN", "Injected Focus Chain instructions into prompt");
        }
        
        return String.join("\n\n", contentParts);
    }
    
    /**
     * 处理LLM响应
     */
    private boolean processLLMResponse(String response) {
        // 检查是否包含工具调用
        boolean didUseTools = containsToolCalls(response);
        
        if (didUseTools) {
            // 提取并执行工具调用
            List<ToolCall> toolCalls = extractToolCalls(response);
            
            for (ToolCall toolCall : toolCalls) {
                executeToolCall(toolCall);
            }
        }
        
        return didUseTools;
    }
    
    /**
     * 执行工具调用 - 关键的Focus Chain更新点
     */
    private void executeToolCall(ToolCall toolCall) {
        try {
            // 执行工具
            String result = executeToolInternal(toolCall);
            
            // ⭐ 关键：处理Focus Chain更新
            // 对应ToolExecutor.handleCompleteBlock中的第380-382行
            if (focusChainSettings.isEnabled() && toolCall.hasTaskProgressParameter()) {
                String taskProgress = toolCall.getTaskProgress();
                focusChainManager.updateFCListFromToolResponse(taskProgress);
            }
            
            callbacks.say("TOOL_RESULT", String.format("Tool %s executed: %s", 
                toolCall.getName(), result));
                
        } catch (Exception e) {
            callbacks.say("ERROR", String.format("Tool execution failed: %s", e.getMessage()));
        }
    }
    
    /**
     * 处理任务完成 - 对应AttemptCompletionHandler
     */
    public void handleAttemptCompletion(String result, String taskProgress) {
        // ⭐ 关键：在用户响应之前更新Focus Chain
        // 对应AttemptCompletionHandler.execute中的第100-102行
        if (focusChainSettings.isEnabled() && taskProgress != null) {
            focusChainManager.updateFCListFromToolResponse(taskProgress);
        }
        
        callbacks.say("COMPLETION_RESULT", result);
        
        // 分析未完成的项目（用于遥测）
        if (focusChainManager != null) {
            focusChainManager.analyzeIncompleteItemsOnCompletion();
        }
    }
    
    // 辅助方法
    private String getEnvironmentDetails() {
        return "Environment: Java " + System.getProperty("java.version") + 
               " on " + System.getProperty("os.name");
    }
    
    private boolean containsToolCalls(String response) {
        // 简化实现：检查是否包含工具调用标记
        return response.contains("tool_use") || response.contains("attempt_completion");
    }
    
    private List<ToolCall> extractToolCalls(String response) {
        // 简化实现：解析工具调用
        List<ToolCall> toolCalls = new ArrayList<>();
        
        // 这里应该实现真正的工具调用解析逻辑
        // 为了演示，创建一个模拟的工具调用
        if (response.contains("attempt_completion")) {
            toolCalls.add(new ToolCall("attempt_completion", response, extractTaskProgress(response)));
        }
        
        return toolCalls;
    }
    
    private String extractTaskProgress(String response) {
        // 简化实现：提取task_progress参数
        // 实际实现应该解析JSON或XML格式的工具调用
        int start = response.indexOf("task_progress:");
        if (start != -1) {
            int end = response.indexOf("\n", start);
            if (end == -1) end = response.length();
            return response.substring(start + "task_progress:".length(), end).trim();
        }
        return null;
    }
    
    private String executeToolInternal(ToolCall toolCall) {
        // 简化实现：执行工具调用
        switch (toolCall.getName()) {
            case "attempt_completion":
                handleAttemptCompletion(toolCall.getParams(), toolCall.getTaskProgress());
                return "Task completion attempted";
            default:
                return "Tool executed: " + toolCall.getName();
        }
    }
    
    private boolean shouldContinueRecursion(String response) {
        // 简化实现：判断是否需要继续递归
        return !response.contains("attempt_completion") && containsToolCalls(response);
    }
    
    private String extractNextUserContent(String response) {
        // 简化实现：提取下一个用户内容
        return "Continue with the task based on the previous response.";
    }
    
    /**
     * 工具调用类
     */
    public static class ToolCall {
        private final String name;
        private final String params;
        private final String taskProgress;
        
        public ToolCall(String name, String params, String taskProgress) {
            this.name = name;
            this.params = params;
            this.taskProgress = taskProgress;
        }
        
        public String getName() { return name; }
        public String getParams() { return params; }
        public String getTaskProgress() { return taskProgress; }
        public boolean hasTaskProgressParameter() { return taskProgress != null; }
    }
}
