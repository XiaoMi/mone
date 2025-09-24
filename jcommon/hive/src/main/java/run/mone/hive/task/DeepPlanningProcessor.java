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
    
    private final LLMTaskProcessor llm;
    private final TaskCallbacks callbacks;
    private final TaskCreator taskCreator;
    private final FocusChainManager focusChainManager;
    
    public DeepPlanningProcessor(LLMTaskProcessor llm, TaskCallbacks callbacks, FocusChainManager focusChainManager) {
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
        
        // 构建调研提示词
        String investigationPrompt = String.format(
            "Please perform a comprehensive codebase investigation for the working directory: %s\n\n" +
            "Analyze the following aspects:\n" +
            "1. Project Structure Analysis - identify language, build system, architecture patterns\n" +
            "2. Dependencies Analysis - list key dependencies and frameworks used\n" +
            "3. Technical Debt Identification - find TODO/FIXME comments and potential issues\n" +
            "4. Code Quality Assessment - identify patterns, conventions, and potential improvements\n" +
            "5. Integration Points - identify external services, APIs, or system boundaries\n\n" +
            "Format your response as a structured investigation report with clear sections and bullet points.\n" +
            "Focus on information that would be relevant for implementing new features or modifications.",
            workingDirectory
        );
        
        // 调用LLM执行深度分析
        String investigationResults = llm.sendMessage(investigationPrompt);
        
        callbacks.say("DEEP_PLANNING", "✅ Codebase investigation completed");
        
        return "=== CODEBASE INVESTIGATION RESULTS ===\n\n" + investigationResults;
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
            "Focus on technical decisions, architecture choices, or requirement clarifications. " +
            "Format your response as a numbered list of questions only, without any additional text.",
            userRequest, investigationResults
        );
        
        // 调用LLM生成问题
        String llmQuestions = llm.sendMessage(questionPrompt);
        
        // 解析LLM返回的问题
        List<String> questions = new ArrayList<>();
        String[] lines = llmQuestions.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && (line.matches("^\\d+\\..*") || line.startsWith("- "))) {
                // 移除编号或bullet point
                String question = line.replaceFirst("^\\d+\\.\\s*", "").replaceFirst("^-\\s*", "");
                if (!question.isEmpty()) {
                    questions.add(question);
                }
            }
        }
        
        // 如果解析失败，使用整个回复作为单个问题
        if (questions.isEmpty()) {
            questions.add(llmQuestions.trim());
        }
        
        StringBuilder clarification = new StringBuilder();
        clarification.append("=== CLARIFICATION QUESTIONS AND ANSWERS ===\n\n");
        
        for (int i = 0; i < questions.size(); i++) {
            String question = questions.get(i);
            callbacks.say("QUESTION", String.format("Q%d: %s", i + 1, question));
            
            // 生成智能默认答案的提示
            String answerPrompt = String.format(
                "For the following question about implementing '%s':\n\n" +
                "Question: %s\n\n" +
                "Based on the investigation results and best practices, provide a recommended answer. " +
                "Keep the answer concise and practical.",
                userRequest, question
            );
            
            // 调用LLM生成答案
            String answer = llm.sendMessage(answerPrompt);
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
        
        // 构建实施计划生成提示
        String planPrompt = String.format(
            "Create a comprehensive implementation plan for the following request: '%s'\n\n" +
            "Based on the codebase investigation:\n%s\n\n" +
            "And the clarification questions and answers:\n%s\n\n" +
            "Generate a detailed implementation plan with the following sections:\n" +
            "[Overview] - Brief description of what will be implemented and why\n" +
            "[Types] - New data structures, interfaces, enums that need to be defined\n" +
            "[Files] - Specific files to be created or modified with their purposes\n" +
            "[Functions] - New functions/methods to be added or existing ones to be modified\n" +
            "[Classes] - New classes to be created and their responsibilities\n" +
            "[Dependencies] - Any new dependencies or libraries needed\n" +
            "[Testing] - Testing strategy including unit, integration, and performance tests\n" +
            "[Implementation Order] - Step-by-step sequence for implementation\n\n" +
            "Make the plan specific to the actual codebase structure and requirements. " +
            "Include realistic file paths, class names, and method signatures where possible.",
            userRequest, investigation, clarification
        );
        
        // 调用LLM生成实施计划
        String llmPlan = llm.sendMessage(planPrompt);
        
        // 构建完整的计划文档
        StringBuilder planContent = new StringBuilder();
        planContent.append("# Implementation Plan\n\n");
        planContent.append(llmPlan);
        
        // 保存计划文件
        Path planPath = Paths.get(workingDirectory, "implementation_plan.md");
        Files.write(planPath, planContent.toString().getBytes("UTF-8"));
        
        callbacks.say("DEEP_PLANNING", "✅ Implementation plan generated and saved");
        
        return planPath.toString();
    }
    
    /**
     * Step 4: 创建实施任务
     */
    private String createImplementationTask(String userRequest, String planFilePath) {
        callbacks.say("DEEP_PLANNING", "🎯 Creating trackable implementation task...");
        
        // 读取生成的实施计划文件内容
        String planContent = "";
        try {
            planContent = Files.readString(Paths.get(planFilePath));
        } catch (IOException e) {
            callbacks.say("WARNING", "Could not read implementation plan file: " + e.getMessage());
        }
        
        // 构建任务进度生成提示
        String taskProgressPrompt = String.format(
            "Based on the implementation plan for '%s', extract and create a task progress checklist.\n\n" +
            "Implementation Plan:\n%s\n\n" +
            "Generate a list of 5-8 specific, actionable task items that represent the key implementation steps. " +
            "Each item should be:\n" +
            "- Specific and measurable\n" +
            "- In logical implementation order\n" +
            "- Focused on deliverable outcomes\n\n" +
            "Format as a simple list, one item per line, without checkboxes or bullets.",
            userRequest, planContent
        );
        
        // 调用LLM生成任务进度列表
        String llmTaskProgress = llm.sendMessage(taskProgressPrompt);
        
        // 解析LLM返回的任务进度项
        List<String> taskProgress = new ArrayList<>();
        String[] lines = llmTaskProgress.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                // 清理可能的编号、bullet points等
                String cleanLine = line.replaceFirst("^\\d+\\.\\s*", "")
                                     .replaceFirst("^-\\s*", "")
                                     .replaceFirst("^\\*\\s*", "");
                if (!cleanLine.isEmpty()) {
                    taskProgress.add(cleanLine);
                }
            }
        }
        
        // 如果解析失败，提供默认任务进度
        if (taskProgress.isEmpty()) {
            taskProgress = List.of(
                "Analyze requirements and design approach",
                "Implement core functionality",
                "Add error handling and validation",
                "Create comprehensive tests",
                "Integration and system testing",
                "Documentation and cleanup"
            );
        }
        
        // 生成增强的任务上下文
        String contextPrompt = String.format(
            "Create a concise task description for implementing '%s'. " +
            "The description should be 1-2 sentences that clearly explain what needs to be accomplished. " +
            "Mention that this task was generated through Deep Planning process.",
            userRequest
        );
        
        String llmContext = llm.sendMessage(contextPrompt);
        String taskContext = llmContext.trim();
        
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
