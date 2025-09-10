package run.mone.hive.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Deep Planning处理器
 * 协调Deep Planning的四个步骤执行
 */
public class DeepPlanningProcessor {
    
    private final LLM llm;
    private final TaskCallbacks callbacks;
    private final TaskCreator taskCreator;
    private final FocusChainManager focusChainManager;
    
    public DeepPlanningProcessor(LLM llm, TaskCallbacks callbacks, FocusChainManager focusChainManager) {
        this.llm = llm;
        this.callbacks = callbacks;
        this.taskCreator = new TaskCreator(llm, callbacks);
        this.focusChainManager = focusChainManager;
    }
    
    /**
     * 执行完整的Deep Planning流程
     * @param userRequest 用户请求
     * @param workingDirectory 工作目录
     * @return 创建的任务ID
     */
    public String executeDeepPlanning(String userRequest, String workingDirectory) {
        try {
            callbacks.say("DEEP_PLANNING", "🔍 Starting Deep Planning process...");
            
            // Step 1: Silent Investigation
            callbacks.say("DEEP_PLANNING", "📋 Step 1: Silent Investigation");
            String investigationResults = performSilentInvestigation(workingDirectory);
            
            // Step 2: Discussion and Questions  
            callbacks.say("DEEP_PLANNING", "💬 Step 2: Discussion and Questions");
            String clarificationResults = conductDiscussionAndQuestions(userRequest, investigationResults);
            
            // Step 3: Create Implementation Plan
            callbacks.say("DEEP_PLANNING", "📝 Step 3: Creating Implementation Plan");
            String planFilePath = createImplementationPlan(userRequest, investigationResults, clarificationResults, workingDirectory);
            
            // Step 4: Create Implementation Task
            callbacks.say("DEEP_PLANNING", "🎯 Step 4: Creating Implementation Task");
            String taskId = createImplementationTask(userRequest, planFilePath);
            
            callbacks.say("DEEP_PLANNING", "✅ Deep Planning completed successfully!");
            callbacks.say("DEEP_PLANNING", "📁 Implementation plan saved to: " + planFilePath);
            callbacks.say("DEEP_PLANNING", "🆔 New task created with ID: " + taskId);
            
            return taskId;
            
        } catch (Exception e) {
            callbacks.say("ERROR", "Deep Planning failed: " + e.getMessage());
            throw new RuntimeException("Deep Planning execution failed", e);
        }
    }
    
    /**
     * Step 1: 执行静默调研
     */
    private String performSilentInvestigation(String workingDirectory) {
        callbacks.say("DEEP_PLANNING", "🔎 Analyzing codebase structure...");
        
        // 这里应该调用LLM执行调研命令
        // 为了演示，我们模拟一些调研结果
        StringBuilder investigation = new StringBuilder();
        investigation.append("=== CODEBASE INVESTIGATION RESULTS ===\n\n");
        
        // 模拟项目结构分析
        investigation.append("## Project Structure Analysis\n");
        investigation.append("- Language: Java\n");
        investigation.append("- Build System: Maven/Gradle\n");
        investigation.append("- Architecture: Layered architecture with focus-chain task management\n\n");
        
        // 模拟依赖分析
        investigation.append("## Dependencies Analysis\n");
        investigation.append("- Core Java libraries\n");
        investigation.append("- File I/O operations\n");
        investigation.append("- Concurrent processing support\n\n");
        
        // 模拟技术债务识别
        investigation.append("## Technical Debt Identified\n");
        investigation.append("- TODO: Add error handling in file operations\n");
        investigation.append("- FIXME: Improve command parsing performance\n\n");
        
        String prompt = "Based on the user's request and the following investigation results, " +
                       "what specific questions should we ask to clarify the implementation approach?\n\n" +
                       investigation.toString();
        
        // 实际实现中，这里会调用LLM进行深度分析
        // String llmResponse = llm.sendMessage(prompt);
        
        return investigation.toString();
    }
    
    /**
     * Step 2: 进行讨论和问题澄清
     */
    private String conductDiscussionAndQuestions(String userRequest, String investigationResults) {
        callbacks.say("DEEP_PLANNING", "❓ Identifying clarification questions...");
        
        // 生成针对性问题的提示
        String questionPrompt = String.format(
            "Based on the user request: '%s'\n\n" +
            "And the investigation results:\n%s\n\n" +
            "Generate 2-3 brief, targeted questions that will influence the implementation plan. " +
            "Focus on technical decisions, architecture choices, or requirement clarifications.",
            userRequest, investigationResults
        );
        
        // 模拟生成的问题
        List<String> questions = List.of(
            "Should the implementation use synchronous or asynchronous processing?",
            "What error handling strategy do you prefer for file operations?",
            "Do you need backward compatibility with existing configurations?"
        );
        
        StringBuilder clarification = new StringBuilder();
        clarification.append("=== CLARIFICATION QUESTIONS AND ANSWERS ===\n\n");
        
        for (int i = 0; i < questions.size(); i++) {
            String question = questions.get(i);
            callbacks.say("QUESTION", String.format("Q%d: %s", i + 1, question));
            
            // 在实际实现中，这里应该等待用户回答
            // 为了演示，我们提供默认答案
            String answer = "Use default recommended approach";
            callbacks.say("ANSWER", String.format("A%d: %s", i + 1, answer));
            
            clarification.append(String.format("Q%d: %s\n", i + 1, question));
            clarification.append(String.format("A%d: %s\n\n", i + 1, answer));
        }
        
        return clarification.toString();
    }
    
    /**
     * Step 3: 创建实施计划文档
     */
    private String createImplementationPlan(String userRequest, String investigation, 
                                          String clarification, String workingDirectory) throws IOException {
        callbacks.say("DEEP_PLANNING", "📋 Generating implementation plan document...");
        
        // 生成实施计划内容
        StringBuilder planContent = new StringBuilder();
        planContent.append("# Implementation Plan\n\n");
        
        // Overview section
        planContent.append("[Overview]\n");
        planContent.append("Implement ").append(userRequest).append(".\n\n");
        planContent.append("This implementation will enhance the existing system by adding the requested functionality ");
        planContent.append("while maintaining compatibility with current architecture patterns.\n\n");
        
        // Types section
        planContent.append("[Types]\n");
        planContent.append("Define new data structures and interfaces for the implementation.\n\n");
        planContent.append("- New interfaces for the requested functionality\n");
        planContent.append("- Data transfer objects for parameter passing\n");
        planContent.append("- Enum types for configuration options\n\n");
        
        // Files section
        planContent.append("[Files]\n");
        planContent.append("File modifications required for the implementation.\n\n");
        planContent.append("New files to be created:\n");
        planContent.append("- src/main/java/com/example/NewFeature.java\n");
        planContent.append("- src/main/java/com/example/NewFeatureConfig.java\n\n");
        planContent.append("Existing files to be modified:\n");
        planContent.append("- src/main/java/com/example/MainClass.java (add integration)\n\n");
        
        // Functions section
        planContent.append("[Functions]\n");
        planContent.append("Function modifications and additions.\n\n");
        planContent.append("New functions:\n");
        planContent.append("- executeNewFeature(parameters) in NewFeature.java\n");
        planContent.append("- validateConfiguration(config) in NewFeatureConfig.java\n\n");
        
        // Classes section
        planContent.append("[Classes]\n");
        planContent.append("Class structure changes.\n\n");
        planContent.append("New classes:\n");
        planContent.append("- NewFeature: Main implementation class\n");
        planContent.append("- NewFeatureConfig: Configuration management\n\n");
        
        // Dependencies section
        planContent.append("[Dependencies]\n");
        planContent.append("No new external dependencies required.\n\n");
        planContent.append("Will use existing Java standard library components.\n\n");
        
        // Testing section
        planContent.append("[Testing]\n");
        planContent.append("Comprehensive testing strategy.\n\n");
        planContent.append("- Unit tests for all new classes and methods\n");
        planContent.append("- Integration tests for system interaction\n");
        planContent.append("- Performance tests for critical paths\n\n");
        
        // Implementation Order section
        planContent.append("[Implementation Order]\n");
        planContent.append("Step-by-step implementation sequence.\n\n");
        planContent.append("1. Create base interfaces and data structures\n");
        planContent.append("2. Implement core functionality classes\n");
        planContent.append("3. Add configuration management\n");
        planContent.append("4. Integrate with existing system\n");
        planContent.append("5. Add comprehensive testing\n");
        planContent.append("6. Update documentation\n");
        
        // 保存计划文件
        Path planPath = Paths.get(workingDirectory, "implementation_plan.md");
        Files.write(planPath, planContent.toString().getBytes("UTF-8"));
        
        return planPath.toString();
    }
    
    /**
     * Step 4: 创建实施任务
     */
    private String createImplementationTask(String userRequest, String planFilePath) {
        callbacks.say("DEEP_PLANNING", "🎯 Creating trackable implementation task...");
        
        // 生成任务进度列表
        List<String> taskProgress = List.of(
            "Create base interfaces and data structures",
            "Implement core functionality classes", 
            "Add configuration management",
            "Integrate with existing system",
            "Add comprehensive testing",
            "Update documentation"
        );
        
        // 创建任务上下文
        String taskContext = String.format(
            "Implement %s according to the detailed implementation plan. " +
            "This task was generated through Deep Planning process and includes comprehensive analysis and planning.",
            userRequest
        );
        
        // 创建任务
        TaskCreator.PlanningResult planningResult = new TaskCreator.PlanningResult(
            taskContext, taskProgress, planFilePath
        );
        
        String taskId = taskCreator.createTaskFromPlanningResult(planningResult);
        
        // 如果Focus Chain启用，更新进度列表
        if (focusChainManager != null) {
            String taskProgressText = String.join("\n", taskProgress.stream()
                .map(item -> "- [ ] " + item)
                .toList());
            focusChainManager.updateFCListFromToolResponse(taskProgressText);
        }
        
        return taskId;
    }
}
