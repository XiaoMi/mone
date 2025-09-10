package run.mone.hive.task;

import java.util.List;

/**
 * 任务创建器
 * 对应Cline中的new_task工具功能
 */
public class TaskCreator {
    
    private final LLM llm;
    private final TaskCallbacks callbacks;
    
    public TaskCreator(LLM llm, TaskCallbacks callbacks) {
        this.llm = llm;
        this.callbacks = callbacks;
    }
    
    /**
     * 创建新任务
     * @param context 任务上下文描述
     * @param taskProgress 任务进度列表（可选，用于Focus Chain）
     * @param planFilePath 计划文件路径（可选，用于Deep Planning）
     * @return 创建的任务ID
     */
    public String createNewTask(String context, List<String> taskProgress, String planFilePath) {
        if (context == null || context.trim().isEmpty()) {
            throw new IllegalArgumentException("Task context is required");
        }
        
        // 生成任务ID
        String taskId = "task-" + System.currentTimeMillis();
        
        // 构建任务描述
        StringBuilder taskDescription = new StringBuilder();
        taskDescription.append(context);
        
        // 如果有计划文件，添加引用
        if (planFilePath != null && !planFilePath.trim().isEmpty()) {
            taskDescription.append("\n\n");
            taskDescription.append("Refer to @").append(planFilePath).append(" for a complete breakdown of the task requirements and steps. ");
            taskDescription.append("You should periodically read this file again.");
        }
        
        // 如果有任务进度，添加到描述中
        if (taskProgress != null && !taskProgress.isEmpty()) {
            taskDescription.append("\n\n");
            taskDescription.append("task_progress Items:\n");
            for (String item : taskProgress) {
                if (!item.trim().startsWith("- ")) {
                    taskDescription.append("- [ ] ").append(item).append("\n");
                } else {
                    taskDescription.append(item).append("\n");
                }
            }
        }
        
        // 建议切换到Act模式
        taskDescription.append("\n\n");
        taskDescription.append("Please switch to 'act mode' to begin implementation if you are currently in 'plan mode'.");
        
        // 通知回调
        callbacks.say("new_task", "Creating new task: " + taskId);
        callbacks.onTaskCompleted(taskId, true);
        
        System.out.println("Created new task: " + taskId);
        System.out.println("Task description: " + taskDescription.toString());
        
        return taskId;
    }
    
    /**
     * 从Deep Planning结果创建任务
     * @param planningResult Deep Planning的结果
     * @return 创建的任务ID
     */
    public String createTaskFromPlanningResult(PlanningResult planningResult) {
        return createNewTask(
            planningResult.getTaskContext(),
            planningResult.getTaskProgress(),
            planningResult.getPlanFilePath()
        );
    }
    
    /**
     * Deep Planning结果类
     */
    public static class PlanningResult {
        private final String taskContext;
        private final List<String> taskProgress;
        private final String planFilePath;
        
        public PlanningResult(String taskContext, List<String> taskProgress, String planFilePath) {
            this.taskContext = taskContext;
            this.taskProgress = taskProgress;
            this.planFilePath = planFilePath;
        }
        
        public String getTaskContext() {
            return taskContext;
        }
        
        public List<String> getTaskProgress() {
            return taskProgress;
        }
        
        public String getPlanFilePath() {
            return planFilePath;
        }
    }
}
